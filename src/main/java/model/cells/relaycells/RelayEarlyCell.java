package model.cells.relaycells;

import model.cells.CellPacket;
import model.payload.relaypayload.RelayPayload;

public class RelayEarlyCell extends RelayCell {

    public RelayEarlyCell(int CIRC_ID, byte RELAY_COMMAND, short STREAM_ID, RelayPayload RELAY_PAYLOAD) {
        super(CIRC_ID, CellPacket.RELAY_EARLY_COMMAND, RELAY_COMMAND, STREAM_ID, RELAY_PAYLOAD);
    }

    public RelayEarlyCell(int CIRC_ID, byte[] payload) {
        super(CIRC_ID, CellPacket.RELAY_EARLY_COMMAND, payload);
    }

}
