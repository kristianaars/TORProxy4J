import crypto.TorDiffieHellman;
import exceptions.PayloadSizeNotFixedException;
import factory.CertificateFactory;
import managers.TLSTrustManager;
import model.*;
import model.Certificate;
import model.cell.*;
import utils.ByteUtils;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;

public class Test {



    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException, CertificateEncodingException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException, PayloadSizeNotFixedException, InvalidKeyException {
        SSLContext ctx = SSLContext.getInstance("TLS");

        TLSTrustManager tm = new TLSTrustManager();
        ctx.init(null, new TrustManager[]{tm}, null);

        TorRelay relay = TorRelays.RELAYS[0];

        InetAddress remoteAddress = relay.getAddress();
        InetAddress ownAddress = InetAddress.getByName("188.113.68.128");

        SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket(relay.getAddressAsString(), relay.getPort());

        socket.startHandshake();

        final byte TOR_PROTOCOL_VERSION = (short) 0x0003;

        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        ByteBuffer pumpBuffer = ByteBuffer.allocate(7);
        pumpBuffer.putShort((short) 0);
        pumpBuffer.put((byte) 0x07);
        pumpBuffer.putShort((short) 0x0002);
        pumpBuffer.putShort(TOR_PROTOCOL_VERSION);
        System.out.println(Arrays.toString(pumpBuffer.array()));
        os.write(pumpBuffer.array());

        InputStream is = socket.getInputStream();

        System.out.println("\nConnected...");

        CellPacket VERSION_RESPONSE = null;
        CertCellPacket CERTS_RESPONSE = null;
        AuthChallengeCellPacket AUTH_CHALLENGE = null;
        CellPacket NETINFO_RESPONSE = null;

        final byte VERSION_COMMAND = 0x07;
        final byte CERTS_COMMAND = (byte) 0x81;
        final byte AUTH_CHALLENGE_COMMAND = (byte) 0x82;
        final byte NETINFO_COMMAND = (byte) 0x08;

        final byte CERT_TYPE_2_RSA1024_IDENTITY = 0x02;
        final byte CERT_TYPE_5_TLS_ED25519_LINK = 0x05;

        while (true) {
            short circID = ByteUtils.toShort((byte) is.read(), (byte) is.read());
            byte command = (byte) is.read();

            short length = 509;
            if(command != NETINFO_COMMAND) {
                //Variable sized Cells
                length = ByteUtils.toShort((byte) is.read(), (byte) is.read());
            }

            if(command == -1) { throw new IOException("Connection was closed"); }

            byte[] payload = new byte[ByteUtils.toUnsigned(length)];
            for(int i = 0; i < payload.length; i++) { payload[i] = (byte) is.read(); }

            switch (command) {
                case VERSION_COMMAND: {
                    VERSION_RESPONSE = new CellPacket(circID, command, payload);
                    System.out.println("Received " + VERSION_RESPONSE);
                    break;
                }

                case CERTS_COMMAND: {
                    CERTS_RESPONSE = new CertCellPacket(circID, command, payload);
                    System.out.println("Received " + CERTS_RESPONSE);
                    break;
                }

                case AUTH_CHALLENGE_COMMAND: {
                    AUTH_CHALLENGE = new AuthChallengeCellPacket(circID, command, payload);
                    System.out.println("Received " + AUTH_CHALLENGE);

                    Certificate identity = CertificateFactory.getInstance().generateCertificate((byte) 0x03);
                    CertCellPacket CERT_ANS = new CertCellPacket((short) 0x00, CERTS_COMMAND, new Certificate[]{identity});

                    NetInfoAddress extAddr = new NetInfoAddress((byte) 0x04, remoteAddress.getAddress());
                    NetInfoAddress[] ownAddr = new NetInfoAddress[] { new NetInfoAddress( (byte) 0x04, ownAddress.getAddress() ) };
                    NetInfo netInfo = new NetInfo(0, extAddr, ownAddr);
                    NetInfoCellPacket NETINFO_ANS = new NetInfoCellPacket((short) 0x00, NETINFO_COMMAND, netInfo);

                    System.out.println("Sending " + NETINFO_ANS);
                    os.write(NETINFO_ANS.generateRawCellPacket());

                    System.out.println("Sending " + CERT_ANS);
                    os.write(CERT_ANS.generateRawCellPacket());


                    Certificate initiatorsIdentityKey = identity;
                    Certificate responderIdentityKey = CERTS_RESPONSE.getCertificate(CERT_TYPE_2_RSA1024_IDENTITY);
                    Certificate TLSLinkCertificate = new Certificate(CERT_TYPE_5_TLS_ED25519_LINK, tm.getTLSHandshakeServerCertificate().getEncoded());

                    TorDiffieHellman dh = new TorDiffieHellman();
                    System.out.println(dh.getCommonSecret(new BigInteger(1, relay.getOnionKey().getEncoded())));


                    break;
                }

                case NETINFO_COMMAND: {
                    NETINFO_RESPONSE = new NetInfoCellPacket(circID, command, payload);
                    System.out.println("Received " + NETINFO_RESPONSE);
                    break;
                }

                default: {
                    CellPacket packet = new CellPacket(circID, command, payload);
                    System.out.println("Received " + packet);
                    break;
                }
            }

        }

    }

    static void write(byte[] b) {

    }
}
