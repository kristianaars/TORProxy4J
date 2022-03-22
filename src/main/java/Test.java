import crypto.NTorHandshake;
import crypto.TorDiffieHellman;
import exceptions.DescriptorFieldNotFoundException;
import exceptions.PayloadSizeNotFixedException;
import factory.CertificateFactory;
import factory.CircIDFactory;
import managers.TLSTrustManager;
import model.*;
import model.Certificate;
import model.cell.*;
import model.payload.Payload;
import model.relay.RelayDescriptor;
import model.relay.TorRelay;
import model.relay.TorRelays;
import utils.ByteUtils;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalTime;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException, CertificateEncodingException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException, PayloadSizeNotFixedException, InvalidKeyException, InvalidKeySpecException, DescriptorFieldNotFoundException {
        SSLContext ctx = SSLContext.getInstance("TLS");

        TLSTrustManager tm = new TLSTrustManager();
        ctx.init(null, new TrustManager[]{tm}, null);

        TorRelay relay = TorRelays.RELAYS[3];

        InetAddress remoteAddress = relay.getAddress();
        InetAddress ownAddress = InetAddress.getByName("188.113.68.128");

        SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket(relay.getAddressAsString(), relay.getPort());

        System.out.println(relay);

        socket.startHandshake();


        final byte TOR_PROTOCOL_VERSION_3 = (short) 0x0003;
        final byte TOR_PROTOCOL_VERSION_4 = (short) 0x0004;

        byte ACTIVE_TOR_PROTOCOL_VERSION = TOR_PROTOCOL_VERSION_3;

        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        ByteBuffer pumpBuffer = ByteBuffer.allocate(7);
        pumpBuffer.putShort((short) 0);
        pumpBuffer.put((byte) 0x07);
        pumpBuffer.putShort((short) 0x0002);
        pumpBuffer.putShort(TOR_PROTOCOL_VERSION_4);
        System.out.println(Arrays.toString(pumpBuffer.array()));
        os.write(pumpBuffer.array());

        InputStream is = socket.getInputStream();

        System.out.println("\nConnected...");

        VersionCellPacket VERSION_RESPONSE = null;
        CertCellPacket CERTS_RESPONSE = null;
        AuthChallengeCellPacket AUTH_CHALLENGE = null;
        CellPacket NETINFO_RESPONSE = null;


        final byte CERT_TYPE_2_RSA1024_IDENTITY = 0x02;
        final byte CERT_TYPE_5_TLS_ED25519_LINK = 0x05;

        final int GLOBAL_CIRC_ID = 0x80000002;

        while (true) {
            int CIRC_ID = 0;

            if(ACTIVE_TOR_PROTOCOL_VERSION == TOR_PROTOCOL_VERSION_3) {
                CIRC_ID = ByteUtils.toShort((byte) is.read(), (byte) is.read());
            } else {
                byte[] circIDB = new byte[]{(byte) is.read(), (byte) is.read(), (byte) is.read(), (byte) is.read()};
                CIRC_ID = ByteUtils.toInt(circIDB);
                System.out.println("CIRC_ID: " + CIRC_ID);
            }

            byte command = (byte) is.read();

            System.out.println("Command: " + ByteUtils.toString(command));

            short length;
            boolean expectFixedPayload = CellPacket.isFixedPacketCell(command);
            if(expectFixedPayload) length = Payload.FIXED_PAYLOAD_SIZE;
            else length = ByteUtils.toShort((byte) is.read(), (byte) is.read());

            if(command == -1) { throw new IOException("Connection was closed"); }

            byte[] payload = new byte[ByteUtils.toUnsigned(length)];
            for(int i = 0; i < payload.length; i++) { payload[i] = (byte) is.read(); }

            switch (command) {
                case CellPacket.VERSION_COMMAND: {
                    VERSION_RESPONSE = new VersionCellPacket(CIRC_ID, payload);

                    if(VERSION_RESPONSE.supportsVersion(TOR_PROTOCOL_VERSION_4)) {
                        ACTIVE_TOR_PROTOCOL_VERSION = TOR_PROTOCOL_VERSION_4;
                        System.out.println("Changed to Tor-Protocol Version " + TOR_PROTOCOL_VERSION_4);
                    }
                    System.out.println("Received " + VERSION_RESPONSE);
                    break;
                }

                case CellPacket.CERTS_COMMAND: {
                    CERTS_RESPONSE = new CertCellPacket(CIRC_ID, payload);
                    System.out.println("Received " + CERTS_RESPONSE);
                    break;
                }

                case CellPacket.AUTH_CHALLENGE_COMMAND: {
                    AUTH_CHALLENGE = new AuthChallengeCellPacket(CIRC_ID, payload);
                    System.out.println("Received " + AUTH_CHALLENGE);

                    break;
                }

                case CellPacket.NETINFO_COMMAND: {
                    NETINFO_RESPONSE = new NetInfoCellPacket(CIRC_ID, payload);
                    System.out.println("Received " + NETINFO_RESPONSE);

                    NetInfoAddress extAddr = new NetInfoAddress((byte) 0x04, remoteAddress.getAddress());
                    NetInfoAddress[] ownAddr = new NetInfoAddress[] { new NetInfoAddress( (byte) 0x04, ownAddress.getAddress() ) };
                    NetInfo netInfo = new NetInfo((int) (System.currentTimeMillis() / 1000L), extAddr, ownAddr);
                    NetInfoCellPacket NETINFO_ANS = new NetInfoCellPacket((short) 0x00, netInfo);

                    System.out.println("Sending " + NETINFO_ANS);
                    os.write(NETINFO_ANS.generateRawCellPacket());


                    NTorHandshake handshake = new NTorHandshake(relay);
                    Create2CellPacket handshakePacket = handshake.getClientInitHandshake(GLOBAL_CIRC_ID);

                    System.out.println("Sending " + ByteUtils.toString(handshakePacket.generateRawCellPacket()));
                    byte[] packet = handshakePacket.generateRawCellPacket();

                    os.write(packet);

                    break;
                }

                default: {
                    CellPacket packet = new CellPacket(CIRC_ID, command, payload);
                    System.out.println("Received " + packet);
                    break;
                }
            }

        }

    }

    static void write(byte[] b) {

    }
}
