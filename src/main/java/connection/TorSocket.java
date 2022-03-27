package connection;

import exceptions.TorException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Implemented with skeleton-code from https://nick-lab.gs.washington.edu/java/jdk1.3.1/guide/rmi/sockettype.doc.html
 *
 */
public class TorSocket extends Socket {

    public static int DEFAULT_RELAY_COUNT = 3;

    private TorStream torStream;
    private Circuit circuit;

    /*
     * No-arg constructor for class CompressionSocket
     */
    public TorSocket() { super(); }

    /*
     * Constructor for class CompressionSocket
     */
    public TorSocket(String host, int port, Circuit circuit) throws IOException {
        super();
        try {
            this.circuit = circuit;
            torStream = circuit.createStream(new InetSocketAddress(host, port));
        } catch (TorException e) {
            e.printStackTrace();
            throw new IOException("Unable to create socket due to a Tor-related error.");
        }
    }

    /*
     * Constructor for class CompressionSocket
     */
    public TorSocket(String host, int port) throws IOException {
        super();
        try {
            this.circuit = CircuitService.getInstance().generateNewCircuit(DEFAULT_RELAY_COUNT);
            torStream = circuit.createStream(new InetSocketAddress(host, port));
        } catch (TorException e) {
            e.printStackTrace();
            throw new IOException("Unable to create socket due to a Tor-related error.");
        }
    }

    /**
     * Returns Input Stream of the related Tor-stream
     *
     * @return TorInputStream of the related Tor-Stream
     * @throws IOException If TorStream is not created/running
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if(torStream == null || torStream.getInputStream() == null) {
            throw new IOException("Tor Stream not running.");
        }
        return torStream.getInputStream();
    }

    /**
     * Returns Input Stream of the related Tor-stream
     *
     * @return OutputStream of the related Tor-Stream
     * @throws IOException If TorStream is not created/running
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if(torStream == null || torStream.getOutputStream() == null) {
            throw new IOException("Tor Stream not running.");
        }
        return torStream.getOutputStream();
    }

    /*
     * Flush the CompressionOutputStream before
     * closing the socket.
     */
    public synchronized void close() throws IOException {
        OutputStream o = getOutputStream();
        o.flush();
        torStream.close();
        super.close();
    }

    public synchronized boolean isClosed() {
        return super.isClosed() || torStream.isClosed();
    }

    /**
     * Closes the underlying TorCircuit and ALL streams associated with it.
     *
     * @throws IOException Input Output error from closing circuit and streams.
     */
    public void closeCircuit() throws IOException {
        close();
        circuit.close();
    }

    public Circuit getTorCircuit() {
        return circuit;
    }
}
