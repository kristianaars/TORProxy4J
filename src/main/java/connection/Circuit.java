package connection;

import connection.relay.TorRelay;

public class Circuit {

    private int CIRC_ID;
    private byte TOR_PROTOCOL_VERSION = ConnectionConstants.TOR_PROTOCOL_VERSION_3;

    private EntryCircuitNode entryNode;
    private CircuitNode[] relays;

    protected Circuit(EntryCircuitNode entryNode, CircuitNode[] relays) {
        this.relays = relays;
        this.entryNode = entryNode;
    }

}
