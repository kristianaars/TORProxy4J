package connection;

import connection.relay.TorRelay;
import crypto.NTorHandshake;

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
}
