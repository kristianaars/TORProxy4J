package model.cells.relaycells;

import model.payload.Payload;

public class EndRelayCell extends RelayCell {

    private final byte END_REASON;

    public EndRelayCell(int CIRC_ID, byte COMMAND, Payload PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);
        END_REASON = this.RELAY_PAYLOAD.getPayload()[0];
    }

    public byte getEND_REASON() {
        return END_REASON;
    }

    @Override
    public String toString() {
        return "EndRelayCell{" +
                "END_REASON=" + END_REASON +
                "} " + super.toString();
    }
}
