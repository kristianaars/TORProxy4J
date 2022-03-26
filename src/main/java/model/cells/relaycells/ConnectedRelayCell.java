package model.cells.relaycells;

import model.payload.Payload;
import model.payload.relaypayload.ConnectedRelayPayload;

public class ConnectedRelayCell extends RelayCell {

    public ConnectedRelayCell(int CIRC_ID, byte COMMAND, Payload PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);

        this.RELAY_PAYLOAD = new ConnectedRelayPayload(RELAY_PAYLOAD);
    }

}
