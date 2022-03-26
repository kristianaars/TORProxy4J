package connection;

import exceptions.StreamIDNotFoundException;
import exceptions.TorException;
import model.cells.relaycells.BeginRelayCell;
import model.cells.relaycells.RelayCell;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TorStreamRouter {

    private final static Logger logger = Logger.getLogger("TorStreamRouter");

    private final static int MAX_STREAM_CONNECTIONS = 512;
    private final TorStreamWorker worker;

    private final ArrayList<TorStream> activeStreams;
    private final Circuit circuit;

    public TorStreamRouter(Circuit circuit) {
        this.worker = new TorStreamWorker(MAX_STREAM_CONNECTIONS);
        this.activeStreams = new ArrayList<>(MAX_STREAM_CONNECTIONS);
        this.circuit = circuit;
        this.worker.start();
    }

    public TorStream createStreamTo(InetSocketAddress address) throws TorException, IOException {
        TorStream newStream = new TorStream(this, address);
        post(newStream);
        newStream.beginRelayConnection();

        return newStream;
    }

    private void post(TorStream stream) {
        worker.post(stream);
        activeStreams.add(stream);
    }

    public int getCircID() {
        return circuit.getCircID();
    }

    public void routeCellPacket(RelayCell packet) throws StreamIDNotFoundException {
        short streamID = packet.getSTREAM_ID();

        if(streamID == 0) {
            logger.info("Unahndeled global stream-packet received " + packet);
            return;
        }

        TorStream stream = activeStreams.stream().filter(s -> s.getStreamID() == streamID).findFirst().orElse(null);

        if(stream == null) {
            throw new StreamIDNotFoundException("Unable to find active stream with id " + streamID);
        }

        stream.postCell(packet);
    }

    public void sendCell(RelayCell sendCell) {
        try {
            circuit.sendCell(sendCell);
        } catch (IOException e) {
            //TODO Better error handling
            e.printStackTrace();
        }
    }
}
