package connection;

import connection.relay.TorRelay;
import crypto.NTorHandshake;
import exceptions.CouldNotVerifyHandshakeException;
import exceptions.UnexpectedDestroyException;
import factory.CircIDFactory;
import model.NetInfo;
import model.NetInfoAddress;
import model.cell.*;
import utils.ByteUtils;
import utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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

    public Circuit buildCircuit() throws IOException, UnexpectedDestroyException, CouldNotVerifyHandshakeException {
        createEntryConnection(entryNode);

        for(int i = 1; i < nodes.length; i++) {
            expandTo(nodes[i]);
        }

        return new Circuit(entryNode, expandedNodes.toArray(new CircuitNode[0]));
    }

    private void createEntryConnection(EntryCircuitNode entryNode) throws IOException, UnexpectedDestroyException, CouldNotVerifyHandshakeException {
        entryNode.initiateTLSConnection();

        NTorHandshake handshake = entryNode.getHandshake();
        CellPacketInputStream inputStream = entryNode.getInputStream();
        CellPacketOutputStream outputStream = entryNode.getOutputStream();

        //Send version cell
        VersionCellPacket VERSION_REQUEST = new VersionCellPacket(0, ConnectionConstants.SUPPORTED_PROTOCOL_VERSIONS);
        outputStream.write(VERSION_REQUEST);

        //Receive version cell and agree on version
        VersionCellPacket VERSION_RESPONSE = (VersionCellPacket) inputStream.getPacket();
        short agreedVersion = VERSION_REQUEST.highestCommonVersion(VERSION_RESPONSE);
        logger.log(Level.INFO, "Agreed upton TOR protocol-version for CIRC_ID " + ByteUtils.toHexString(CIRC_ID) + " is " + agreedVersion);
        entryNode.setTorProtocolVersion(agreedVersion);

        //Wait until expected cells are received (Cert, Auth and NetInfo)
        CertCellPacket CERT_CELL_RESPONSE = null;
        AuthChallengeCellPacket AUTH_CELL_RESPONSE = null;
        NetInfoCellPacket NET_INFO_RESPONSE = null;

        while (CERT_CELL_RESPONSE == null || AUTH_CELL_RESPONSE == null || NET_INFO_RESPONSE == null) {
            CellPacket packet = inputStream.getPacket();

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
                    outputStream.write(NET_INFO_ANSWER);
                    break;
            }
        }



        //Create and send CREATE-Cell to initiate TOR-Handshake
        Create2CellPacket CREATE_CELL_ANSWER = handshake.getClientInitHandshake(CIRC_ID);
        outputStream.write(CREATE_CELL_ANSWER);

        //Expect a Created cell, if Destroy is received something must have gone wrong...
        CellPacket packet = inputStream.getPacket();

        switch (packet.getCOMMAND()) {
            case CellPacket.CREATED2_COMMAND:
                handshake.provideServerHandshakeResponse((Created2CellPacket) packet);
                //Handshake is now complete!
                expandedNodes.add(entryNode);
                break;
            case CellPacket.DESTROY_COMMAND:
                DestroyCellPacket destroyCell = (DestroyCellPacket) packet;
                //TODO: Close connection
                throw new UnexpectedDestroyException("Created2 cell was expected, but received destroy-cell with reason " + destroyCell.getDESTROY_REASON());
        }

    }

    private void expandTo(CircuitNode node) throws IOException, UnexpectedDestroyException {
        CircuitNode originNode = expandedNodes.get(expandedNodes.size() - 1);

        NTorHandshake originHandshake = originNode.getHandshake();
        NTorHandshake newHandshake = node.getHandshake();

        Extend2RelayCell extCell = newHandshake.getExtendCell(CIRC_ID);

        sendCellAcrossCircuit(extCell);

        CellPacketInputStream inputStream = entryNode.getInputStream();
        CellPacket expPacket = inputStream.getPacket();

        if(expPacket.getCOMMAND() == RelayCell.DESTROY_COMMAND) {
            throw new UnexpectedDestroyException("Received unexpected Destroy-Command when expecting command with value " + RelayCell.RELAY_EARLY);
        }

        logger.info("Received EXP-Packet " + expPacket);
    }

    public void sendCellAcrossCircuit(RelayCell cell) throws IOException {
        for(int i = expandedNodes.size() - 1; i >= 0; i--) {
            CircuitNode node = expandedNodes.get(i);
            logger.info("Encrypting cell for node #" + i);
            node.encryptCell(cell);

        }

        entryNode.getOutputStream().write(cell);
    }

    private NetInfoCellPacket getNetInfoAnswer(TorRelay remoteRelay) {
        NetInfoAddress extAddr = new NetInfoAddress((byte) 0x04, remoteRelay.getAddress().getAddress());
        NetInfoAddress[] ownAddr = new NetInfoAddress[] { new NetInfoAddress( (byte) 0x04, NetworkUtils.getPublicIP().getAddress()) };
        return new NetInfoCellPacket((short) 0x00, new NetInfo((int) (System.currentTimeMillis() / 1000L), extAddr, ownAddr));
    }
}
