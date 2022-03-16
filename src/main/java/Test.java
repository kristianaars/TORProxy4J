import factory.CertificateFactory;
import model.cell.AuthChallengeCellPacket;
import model.cell.CellPacket;
import model.cell.CertCellPacket;
import model.cell.CertPayload;
import model.cell.Certificate;
import utils.ByteUtils;

import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("TLS");

        TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
                System.out.println("TLS-Certificate to relay: " + chain[chain.length - 1]);
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        ctx.init(null, new TrustManager[]{tm}, null);

        SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket("136.243.4.139", 8008);
        socket.startHandshake();

        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        ByteBuffer pumpBuffer = ByteBuffer.allocate(7);
        pumpBuffer.putShort((short) 0);
        pumpBuffer.put((byte) 0x07);
        pumpBuffer.putShort((short) 0x0002);
        pumpBuffer.putShort((short) 0x0003);
        System.out.println(Arrays.toString(pumpBuffer.array()));
        os.write(pumpBuffer.array());
        //os.flush();

        //InputStreamReader ir = new InputStreamReader(socket.getInputStream());
        InputStream is = socket.getInputStream();

        //Socket socket = new Socket("45.66.33.45", 443);
        System.out.println("\nConnected");

        CellPacket VERSION_RESPONSE = null;
        CertCellPacket CERTS_RESPONSE = null;
        AuthChallengeCellPacket AUTH_CHALLENGE = null;
        CellPacket NETINFO_RESPONSE = null;

        final byte VERSION_COMMAND = 0x07;
        final byte CERTS_COMMAND = (byte) 0x81;
        final byte AUTH_CHALLENGE_COMMAND = (byte) 0x82;
        final byte NETINFO_COMMAND = (byte) 0x08;

        while (true) {
            //System.out.print(String.format("0x%02X ", (byte) is.read()));
            short circID = (short) ((short) (is.read() << 8) ^ is.read());
            byte command = (byte) is.read();
            short length = (short) ((short) (is.read() << 8) ^ is.read());

            if(command == -1) { throw new IOException("Connection was closed"); }

            byte[] payload = new byte[ByteUtils.toUnsigned(length)];
            for(int i = 0; i < payload.length; i++) { payload[i] = (byte) is.read(); }

            switch (command) {
                case VERSION_COMMAND -> VERSION_RESPONSE = new CellPacket(circID, command, payload);
                case CERTS_COMMAND -> CERTS_RESPONSE = new CertCellPacket(circID, command, payload);
                case AUTH_CHALLENGE_COMMAND -> AUTH_CHALLENGE = new AuthChallengeCellPacket(circID, command, payload);
                case NETINFO_COMMAND -> NETINFO_RESPONSE = new CellPacket(circID, command, payload);
            }

            System.out.println("Received command: " + ByteUtils.toString(command));

            if(VERSION_RESPONSE != null && CERTS_RESPONSE != null && AUTH_CHALLENGE != null && NETINFO_RESPONSE == null) {
                Certificate identity = CertificateFactory.getInstance().generateCertificate((byte) 0x03);
                CertCellPacket CERT_ANS = new CertCellPacket((short) 0x00, (byte) 0x81, new Certificate[]{identity});

                System.out.println("Sending " + ByteUtils.toString(CERT_ANS.generateRawCellPacket()));
                os.write(CERT_ANS.generateRawCellPacket());
            }
        }
    }
}
