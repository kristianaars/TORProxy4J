package connection;

import exceptions.TorException;
import factory.StreamIDFactory;
import model.cells.CellPacket;
import model.cells.relaycells.*;
import utils.BlockingBuffer;
import utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class TorStream implements Runnable {

    private static final Logger logger = Logger.getLogger("TorStream");
    private final BlockingBuffer<CellPacket> packetBuffer;

    private final short STREAM_ID;
    private final TorStreamRouter router;
    private final InetSocketAddress address;

    private final TorInputStream inputStream;
    private final TorOutputStream outputStream;

    private boolean isRunning = true;
    private ReentrantLock isRunningLock;

    public TorStream(TorStreamRouter router, InetSocketAddress address) {
        super();
        this.STREAM_ID = StreamIDFactory.getInstance().generateStreamID();
        this.router = router;
        this.address = address;
        this.packetBuffer = new BlockingBuffer<>();
        this.inputStream = new TorInputStream();
        this.outputStream = new TorOutputStream(this);

        this.isRunningLock = new ReentrantLock();
    }

    public void beginRelayConnection() throws IOException, TorException {
        BeginRelayCell sendCell = BeginRelayCell.generateBeginRelayCell(router.getCircID(), STREAM_ID, address.getHostString(), address.getPort());
        router.sendCell(sendCell);

    }

    protected void sendData(byte[] b) throws IOException {
        DataRelayCell c = DataRelayCell.createFrom(b, router.getCircID(), STREAM_ID);
        System.out.println(ByteUtils.toHexString(c.getData()));
        router.sendCell(c);
    }

    private CellPacket getCell() {
        return packetBuffer.poll();
    }

    /**
     * Post cell to internal cell-buffer to be processed for the input-buffer of the stream.
     *
     * @param packet Packet recevied to the cell.
     */
    protected void postCell(RelayCell packet) {
        packetBuffer.insert(packet);
    }

    public short getStreamID() {
        return STREAM_ID;
    }

    @Override
    public void run() {

        isRunningLock.lock();
        isRunning = true;
        isRunningLock.unlock();

        while (isRunning) {
            CellPacket rec = getCell();
            if (rec instanceof ConnectedRelayCell) {
                logger.info("Successfully connected to " + address + " with streamID " + ByteUtils.toHexString(STREAM_ID));
                //Connection successful
            } else if (rec instanceof DataRelayCell) {
                inputStream.post(((DataRelayCell) rec).getData());
            } else if(rec instanceof EndRelayCell) {
                try {
                    logger.info("Received end-stream message from circuit. Closing stream... End reason: " + ((EndRelayCell) rec).getEND_REASON());
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if(rec == null && !isRunning) {
                    break;
                }

                try {
                    throw new TorException("Unable to handle RelayCell because it was of unknown type " + rec);
                } catch (TorException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public TorInputStream getInputStream() {
        return inputStream;
    }

    public TorOutputStream getOutputStream() {
        return outputStream;
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();

        isRunningLock.lock();
        isRunning = false;
        isRunningLock.unlock();

        packetBuffer.insert(null);
    }

    public boolean isClosed() {
        isRunningLock.lock();
        boolean ret = !isRunning;
        isRunningLock.unlock();
        return ret;
    }
}
