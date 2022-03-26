package connection;

import connection.relay.TorRelay;
import crypto.NTorHandshake;
import crypto.RelayDigest;
import exceptions.DecryptionException;
import model.cells.relaycells.RelayCell;
import model.payload.Payload;

public class CircuitNode {

    private final TorRelay relay;
    private final NTorHandshake handshake;

    public CircuitNode(TorRelay relay) {
        this.relay = relay;
        this.handshake = new NTorHandshake(relay);
    }

    public TorRelay getRelay() {
        return relay;
    }

    public NTorHandshake getHandshake() {
        return handshake;
    }

    public RelayCell encryptCell(RelayCell cell) {
        Payload encryptedPayload = handshake.encryptPayload(cell.getPayload());
        return new RelayCell(cell.getCIRC_ID(), cell.getCOMMAND(), encryptedPayload);
    }

    public RelayCell decryptCell(RelayCell relayCell) throws DecryptionException {
        Payload decryptedPayload = handshake.decryptPayload(relayCell.getPayload());
        return new RelayCell(relayCell.getCIRC_ID(), relayCell.getCOMMAND(), decryptedPayload);
    }

    public RelayDigest getFwDigest() {
        return handshake.getFwDigest();
    }

    public RelayDigest getBwDigest() {
        return handshake.getBwDigest();
    }

    public String getFingerprint() {
        return relay.getFingerprint();
    }

    @Override
    public String toString() {
        return "CircuitNode{" +
                "relay=" + relay +
                ", handshake=" + handshake +
                '}';
    }
}
