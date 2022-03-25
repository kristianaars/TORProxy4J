package connection;

import connection.relay.TorRelay;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class EntryCircuitNode extends CircuitNode {

    private static final Logger logger = Logger.getLogger("EntryCircuitNode");

    private Socket connectionSocket;

    private CellPacketInputStream inputStream;
    private CellPacketOutputStream outputStream;

    /**
     * Describes if the TOR-Connection is created. This requires a successful NTOR-Handshake.
     */
    private boolean isTorConnectionCreated = false;

    public EntryCircuitNode(TorRelay relay) {
        super(relay);
    }

    public boolean initiateTLSConnection() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            TLSTrustManager tm = new TLSTrustManager();
            ctx.init(null, new TrustManager[]{tm}, null);

            String relayAddress = getRelay().getAddressAsString();
            int relayPort = getRelay().getPort();

            logger.info("Performing TLS Handshake with " + relayAddress + ":" + relayPort);

            SSLSocket socket = (SSLSocket) ctx.getSocketFactory().createSocket(relayAddress, relayPort);

            socket.startHandshake();

            connectionSocket = socket;
            inputStream = new CellPacketInputStream(connectionSocket.getInputStream());
            outputStream = new CellPacketOutputStream(connectionSocket.getOutputStream());

            if(socket.isConnected()) return true;
            else return false;
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void setTorProtocolVersion(short version) {
        inputStream.setTorProtocolVersion(version);
        outputStream.setTorProtocolVersion(version);
    }

    public CellPacketInputStream getInputStream() {
        return inputStream;
    }

    public CellPacketOutputStream getOutputStream() {
        return outputStream;
    }

    public boolean isTorConnectionCreated() {
        return isTorConnectionCreated;
    }

    public void setTorConnectionCreated(boolean torConnectionCreated) {
        isTorConnectionCreated = torConnectionCreated;
    }

    @Override
    public String toString() {
        return "EntryCircuitNode{" +
                "isTorConnectionCreated=" + isTorConnectionCreated +
                "} " + super.toString();
    }
}
