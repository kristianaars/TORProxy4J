package model.cell;

import model.payload.RelayData;

public class RelayEarlyCell extends RelayCell {

    public RelayEarlyCell(int CIRC_ID , byte[] PAYLOAD) {
        super(CIRC_ID, CellPacket.RELAY_EARLY, PAYLOAD);
    }

    public RelayEarlyCell(int CIRC_ID, byte RELAY_COMMAND, short STREAM_ID, int DIGEST, RelayData DATA) {
        super(CIRC_ID, CellPacket.RELAY_EARLY, RELAY_COMMAND, STREAM_ID, DIGEST, DATA);
    }
}
