package model.cell;

import model.payload.Created2Payload;

public class Created2CellPacket extends CellPacket {

    public Created2CellPacket(int CIRC_ID, byte[] PAYLOAD) {
        super(CIRC_ID, CellPacket.CREATED2_COMMAND, PAYLOAD);

        this.PAYLOAD = new Created2Payload(this.PAYLOAD);
    }
}
