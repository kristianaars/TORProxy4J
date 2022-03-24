package connection;

import connection.relay.TorRelay;
import crypto.NTorHandshake;
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

    public void encryptCell(RelayCell cell) {
        if(cell.getDIGEST() == 0) {
            cell.calculateAndSetDigestValue(handshake.getKeyMaterial().getDF());
        }

        Payload encryptedPayload = handshake.encryptPayload(cell.getPayload());
        System.out.println("Encrypted payload " + ByteUtils.toHexString(encryptedPayload.getPayload()));
        cell.setPayload(encryptedPayload);
    }

}
