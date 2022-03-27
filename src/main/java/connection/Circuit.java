package connection;

import connection.relay.TorRelay;
import exceptions.DecryptionException;
import exceptions.NodeNotConnectedException;
import exceptions.StreamIDNotFoundException;
import exceptions.TorException;
import model.cells.CellPacket;
import model.cells.relaycells.*;
import utils.ByteUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Circuit {

    public boolean isRunning = false;
    private static final Logger logger =  Logger.getLogger("Circuit");

    private final int CIRC_ID;
    private short TOR_PROTOCOL_VERSION = ConnectionConstants.TOR_PROTOCOL_VERSION_3;

    private final ArrayList<CircuitNode> nodes;
    private final TorStreamRouter streamRouter;

    protected Circuit(EntryCircuitNode entryNode, int CIRC_ID) {
        this.CIRC_ID = CIRC_ID;
        this.streamRouter = new TorStreamRouter(this);
        nodes = new ArrayList<>();
        nodes.add(entryNode);
    }

    public void addNode(CircuitNode node) throws NodeNotConnectedException {
        if(node.getHandshake().getKeyMaterial() == null) {
            throw new NodeNotConnectedException("The node with fingerprint " + node.getRelay().getFingerprint() + " was added to a circuit without being extended to.");
        }

        nodes.add(node);
        logger.info("Node with fingerprint " + node.getFingerprint() + " was successfully added to the circuit");
    }

    public TorStream createStream(InetSocketAddress toAddress) throws TorException, IOException {
        return streamRouter.createStreamTo(toAddress);
    }

    public EntryCircuitNode getEntryNode() {
        return (EntryCircuitNode) nodes.get(0);
    }

    public int getCircID() {
        return CIRC_ID;
    }

    public short getTorProtocolVersion() {
        return TOR_PROTOCOL_VERSION;
    }

    public CellPacket getCell() throws IOException, TorException {
        CellPacket cell = getEntryNode().getInputStream().getPacket();

        if(cell instanceof RelayCell) {
            try {
                return decryptRelayCell((RelayCell) cell);
            }  catch (DecryptionException e) {
                this.close();
                throw e;
            }

        } else {
            return cell;
        }
    }

    public void sendCell(CellPacket cell) throws IOException {
        if(cell instanceof RelayCell) {
            if(!(((RelayCell) cell).isEncrypted()) && ((RelayCell) cell).getDIGEST() != 0) {
                throw new IOException("Cell with digest, should not have it...");
            }

            logger.info("Encrypt and send: " + cell);
            getEntryNode().getOutputStream().write(
                    encryptCellPacket((RelayCell) cell)
            );

        } else {
            logger.info("Send: " + cell);
            getEntryNode().getOutputStream().write(cell);
        }
    }

    private boolean verifyCellDigest(CircuitNode node, RelayCell cell) {
        int verifyDigestValue = cell.getDIGEST();
        cell.setDigestValue(0);
        //cell.calculateAndSetDigestValue(node.getHandshake().getKeyMaterial().getDB());

        return cell.getDIGEST() == verifyDigestValue;
    }

    private RelayCell decryptRelayCell(RelayCell relayCell) throws DecryptionException {
        RelayCell cell = relayCell;
        CircuitNode node = nodes.get(0);
        for (int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);

            cell = node.decryptCell(cell);

            if(!cell.isEncrypted()) {

                if(verifyCellDigest(node, cell)) {
                    logger.log(Level.WARNING, "RelayCell was decrypted after " + (i) + " onion skins were removed. Total node-count is " + nodes.size());
                    i = nodes.size();
                    break;
                }

            }
        }

        if(cell.getRECOGNIZED() != 0) {
            throw new DecryptionException("Unable to decrypt onion skins of incoming Relay Cell");
        } else if(!verifyCellDigest(node, cell)) {
            throw new DecryptionException("Digest of decrypted cell could not be correctly verified.");
        }

        //TODO: Create external relay cell parser
        switch (cell.getRELAY_COMMAND()) {
            case RelayCell.RELAY_COMMAND_DATA:
                return new DataRelayCell(cell.getCIRC_ID(), cell.getCOMMAND(), cell.getPayload());
            case RelayCell.RELAY_COMMAND_EXTENDED2:
                return new Extended2RelayCell(cell.getCIRC_ID(), cell.getCOMMAND(), cell.getPayload());
            case RelayCell.RELAY_COMMAND_CONNECTED:
                return new ConnectedRelayCell(cell.getCIRC_ID(), cell.getCOMMAND(), cell.getPayload());
            case RelayCell.RELAY_COMMAND_END:
                return new EndRelayCell(cell.getCIRC_ID(), cell.getRELAY_COMMAND(), cell.getPayload());
            default:
                return cell;
        }
    }

    private RelayCell encryptCellPacket(RelayCell relayCell) {
        RelayCell cell = relayCell;
        int hops = 0;

        //Set Digest value
        if(relayCell.getDIGEST() == 0) {
            CircuitNode node = nodes.get(nodes.size() - 1);
            byte[] digest = node.getFwDigest().update(relayCell.getPayload().getPayload());
            relayCell.setDigestValue(ByteUtils.toInt(new byte[]{digest[0], digest[1], digest[2], digest[3]}));
        }

        for(int i = nodes.size() - 1; i >= 0; i--) {
            cell = nodes.get(i).encryptCell(cell);
        }

        return cell;
    }

    public void setTorProtocolVersion(short version) {
        logger.log(Level.INFO, "Setting TOR-Protocol version to " + version);
        this.TOR_PROTOCOL_VERSION = version;
        getEntryNode().setTorProtocolVersion(version);
    }

    public void startPacketListenerLoop() {
        new Thread(() -> {
            isRunning = true;
            while (isRunning) {
                CellPacket packet = null;
                try {
                    packet = getCell();
                } catch (IOException | TorException e) {
                    e.printStackTrace();
                    isRunning = false;
                    break;
                }

                if(packet instanceof RelayCell) {
                    try {
                        streamRouter.routeCellPacket((RelayCell) packet);
                    } catch (StreamIDNotFoundException e) {
                        e.printStackTrace();
                        isRunning = false;
                        break;
                    }
                } else {
                    if(packet==null && !isRunning) {
                        break;
                    }

                    logger.log(Level.WARNING, "Received cell packet with no handle-plan " + packet);
                }
            }
        }).start();

    }

    public void close() throws IOException {
        logger.info("Closing circuit wiht CircID " + getCircID());
        isRunning = false;
        streamRouter.closeAllStreams();
        getEntryNode().close();
    }

    public int getRelayCount() {
        return nodes.size();
    }

    public String getRelayInfo() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            if(i == 0) b.append("Entry node: ");
            else if(i == nodes.size() - 1) b.append("Exit node: ");
            else b.append("Relay node: ");

            b.append(nodes.get(i).getRelay().toString() + "\n");
        }

        return b.toString();
    }
}
