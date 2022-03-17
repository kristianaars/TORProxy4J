package model.cell;

import model.payload.Create2Payload;

public class Create2CellPacket extends CellPacket {

    public Create2CellPacket(short CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);

        this.PAYLOAD = new Create2Payload(this.PAYLOAD);
    }


}
