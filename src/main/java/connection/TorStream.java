package connection;

import exceptions.TorException;
import factory.StreamIDFactory;
import model.cells.CellPacket;
import model.cells.relaycells.BeginRelayCell;
import model.cells.relaycells.ConnectedRelayCell;
import model.cells.relaycells.DataRelayCell;
import model.cells.relaycells.RelayCell;
import utils.BlockingBuffer;
import utils.ByteUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class TorStream implements Runnable {

    private static final Logger logger = Logger.getLogger("TorStream");
    private final BlockingBuffer<CellPacket> packetBuffer;

    private final short STREAM_ID;
    private final TorStreamRouter router;
    private final InetSocketAddress address;

    public TorStream(TorStreamRouter router, InetSocketAddress address) {
        this.STREAM_ID = StreamIDFactory.getInstance().generateStreamID();
        this.router = router;
        this.address = address;
        this.packetBuffer = new BlockingBuffer<>();
    }

    public void beginRelayConnection() throws IOException, TorException {
        BeginRelayCell sendCell = BeginRelayCell.generateBeginRelayCell(router.getCircID(), STREAM_ID, address.getHostString(), address.getPort());
        router.sendCell(sendCell);

    }

    public void sendData(byte[] b) {
        DataRelayCell c = DataRelayCell.createFrom(b, router.getCircID(), STREAM_ID);
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
        logger.info("Received data on stream: " + ByteUtils.toHexString(packet.getRelayPayload().getPayload()));
    }

    public short getStreamID() {
        return STREAM_ID;
    }

    @Override
    public void run() {
        boolean isRunning = true;

        while (isRunning) {
            CellPacket rec = getCell();
            logger.info("Received packet on stream " + rec);

            if (rec instanceof ConnectedRelayCell) {
                logger.info("Sucsesfully connected to " + address + " with streamID " + ByteUtils.toHexString(STREAM_ID));
                //Connection successful
            } else {
                isRunning = false;
                new TorException("Unable to create stream to " + address).printStackTrace();
            }
        }
    }
}
