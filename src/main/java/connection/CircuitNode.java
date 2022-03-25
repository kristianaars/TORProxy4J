package connection;

import connection.relay.TorRelay;
import crypto.NTorHandshake;
import exceptions.DecryptionException;
import model.cell.RelayCell;
import model.payload.Payload;
import model.payload.RelayPayload;
import utils.ByteUtils;

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
        if(cell.getDIGEST() == 0 && !cell.isEncrypted()) {
            cell.calculateAndSetDigestValue(handshake.getKeyMaterial().getDF());
        }

        Payload encryptedPayload = handshake.encryptPayload(cell.getPayload());
        return new RelayCell(cell.getCIRC_ID(), cell.getCOMMAND(), encryptedPayload);
    }

    public RelayCell decryptCell(RelayCell relayCell) throws DecryptionException {
        Payload decryptedPayload = handshake.decryptPayload(relayCell.getPayload());

        RelayCell decryptedCell = new RelayCell(relayCell.getCIRC_ID(), relayCell.getCOMMAND(), decryptedPayload);

        if(!decryptedCell.isEncrypted()) {
            //Verify
            int verifyDigestValue = decryptedCell.getDIGEST();
            decryptedCell.setDigestValue(0);
            decryptedCell.calculateAndSetDigestValue(handshake.getKeyMaterial().getDB());

            if(decryptedCell.getDIGEST() != verifyDigestValue) {
                throw new DecryptionException("Unabel to verify Digest-value of unencrypted cell");
            }
        }

        return decryptedCell;
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
