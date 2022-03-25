package connection;

import connection.relay.TorRelay;
import crypto.NTorHandshake;
import exceptions.*;
import factory.CircIDFactory;
import model.NetInfo;
import model.NetInfoAddress;
import model.cell.*;
import model.payload.Extended2RelayPayload;
import model.payload.Payload;
import utils.ByteUtils;
import utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CircuitBuilder {

    private static final Logger logger = Logger.getLogger("CircuitBuilder");

    private List<CircuitNode> expandedNodes;

    private CircuitNode[] nodes;
    private EntryCircuitNode entryNode;
    private int CIRC_ID = 0;

    public CircuitBuilder(TorRelay[] relays) {
        this.nodes = new CircuitNode[relays.length];
        this.expandedNodes = new ArrayList<>();

        int i = 0;
        for (TorRelay tr : relays) {
            if(i == 0) { nodes[i++] = new EntryCircuitNode(tr); }
            else { nodes[i++] = new CircuitNode(tr); }
        }

        entryNode = (EntryCircuitNode) nodes[0];
        CIRC_ID = CircIDFactory.getInstance().getCircID();
    }

    public Circuit buildCircuit() throws IOException, TorException {
        Circuit circuit = new Circuit(entryNode, CIRC_ID);

        logger.info("Building circuit " + ByteUtils.toHexString(CIRC_ID) + " with entry-node " + entryNode.getFingerprint() + "...");
        createEntryConnection(circuit);

        if(entryNode.getHandshake().getKeyMaterial() != null) {
            entryNode.setTorConnectionCreated(true);
        } else {
            throw new NTorHandshakeException("Could not find key-material after handshake with Entry-Node was completed");
        }

        for(int i = 1; i < nodes.length; i++) {
            expandTo(nodes[i], circuit);
        }

        return circuit;
    }

    private void createEntryConnection(Circuit circuit) throws IOException, TorException {
        EntryCircuitNode entryNode = circuit.getEntryNode();
        entryNode.initiateTLSConnection();

        NTorHandshake handshake = entryNode.getHandshake();
        CellPacketInputStream inputStream = entryNode.getInputStream();
        CellPacketOutputStream outputStream = entryNode.getOutputStream();

        //Send version cell
        logger.info("Negotiating TOR-Protocol versions...");
        VersionCellPacket VERSION_REQUEST = new VersionCellPacket(0, ConnectionConstants.SUPPORTED_PROTOCOL_VERSIONS);
        circuit.sendCell(VERSION_REQUEST);

        //Receive version cell and agree on version
        VersionCellPacket VERSION_RESPONSE = (VersionCellPacket) inputStream.getPacket();

        //Set TOR-Protocol version to the highest common version-number.
        circuit.setTorProtocolVersion(VERSION_REQUEST.highestCommonVersion(VERSION_RESPONSE));

        //Wait until expected cells are received (Cert, Auth and NetInfo)
        CertCellPacket CERT_CELL_RESPONSE = null;
        AuthChallengeCellPacket AUTH_CELL_RESPONSE = null;
        NetInfoCellPacket NET_INFO_RESPONSE = null;

        logger.info("Waiting for initialization Cells...");
        while (CERT_CELL_RESPONSE == null || AUTH_CELL_RESPONSE == null || NET_INFO_RESPONSE == null) {
            CellPacket packet = circuit.getCell();

            switch (packet.getCOMMAND()) {
                case CellPacket.CERTS_COMMAND:
                    CERT_CELL_RESPONSE = (CertCellPacket) packet;
                    break;
                case CellPacket.AUTH_CHALLENGE_COMMAND:
                    AUTH_CELL_RESPONSE = (AuthChallengeCellPacket) packet;
                    break;
                case CellPacket.NETINFO_COMMAND:
                    NET_INFO_RESPONSE = (NetInfoCellPacket) packet;

                    //Send NetInfo client-answer to server
                    NetInfoCellPacket NET_INFO_ANSWER = getNetInfoAnswer(entryNode.getRelay());
                    circuit.sendCell(NET_INFO_ANSWER);
                    break;
            }
        }

        //Create and send CREATE-Cell to initiate TOR-Handshake
        Create2CellPacket CREATE_CELL_ANSWER = handshake.getClientInitHandshake(CIRC_ID);
        circuit.sendCell(CREATE_CELL_ANSWER);

        //Expect a Created cell, if Destroy is received something must have gone wrong...
        CellPacket createdCell = circuit.getCell();

        if(createdCell instanceof Created2CellPacket) {
            handshake.provideServerHandshakeResponse(((Created2CellPacket) createdCell).getHandshakeResponse());
            //Handshake is now complete!
            logger.info("Successfully created TOR-Connection to entry node");
        } else {
            if(createdCell instanceof DestroyCellPacket) {
                DestroyCellPacket destroyCell = (DestroyCellPacket) createdCell;
                throw new UnexpectedDestroyException("Created2 cell was expected, but received DestroyCell with destroy-reason " + destroyCell.getDESTROY_REASON());
            } else {
                throw new UnexpectedCellPacketTypeException("Created2 cell was expected, but received cell with command number " + createdCell.getCOMMAND());
            }
        }
    }

    private void expandTo(CircuitNode node, Circuit circuit) throws IOException, TorException {
        NTorHandshake newHandshake = node.getHandshake();

        Extend2RelayCell extCell = newHandshake.getExtendCell(CIRC_ID);
        logger.info("Sending Extendcell " + extCell);
        circuit.sendCell(extCell);

        CellPacket response = circuit.getCell();

        if(!(response instanceof Extended2RelayCell)) {
            if(response.getCOMMAND() == RelayCell.DESTROY_COMMAND) {
                throw new UnexpectedDestroyException("Received unexpected Destroy-Command when expecting command with value " + RelayCell.RELAY_EARLY_COMMAND);
            } else {
                throw new UnexpectedCellPacketTypeException("Received unexpected CellPacket of type " + response.getClass().getName() + " when expecting a Extended2RelayCell");
            }
        }

        Extended2RelayCell extended2Response = (Extended2RelayCell) response;
        newHandshake.provideServerHandshakeResponse(extended2Response.getHandshakeResponse());


        circuit.addNode(node);
    }

    private NetInfoCellPacket getNetInfoAnswer(TorRelay remoteRelay) {
        NetInfoAddress extAddr = new NetInfoAddress((byte) 0x04, remoteRelay.getAddress().getAddress());
        NetInfoAddress[] ownAddr = new NetInfoAddress[] { new NetInfoAddress( (byte) 0x04, NetworkUtils.getPublicIP().getAddress()) };
        return new NetInfoCellPacket((short) 0x00, new NetInfo((int) (System.currentTimeMillis() / 1000L), extAddr, ownAddr));
    }
}
