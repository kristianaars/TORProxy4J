package model.cells;

import model.payload.Create2Payload;

public class Create2CellPacket extends CellPacket {

    public Create2CellPacket(int CIRC_ID, byte[] PAYLOAD) {
        super(CIRC_ID, CellPacket.CREATE2_COMMAND, PAYLOAD);

        this.PAYLOAD = new Create2Payload(this.PAYLOAD);
    }

    public Create2CellPacket(int CIRC_ID, Create2Payload PAYLOAD) {
        super(CIRC_ID, CellPacket.CREATE2_COMMAND, PAYLOAD.getPayload());

        this.PAYLOAD = new Create2Payload(this.PAYLOAD);
    }

    @Override
    public String toString() {
        return "Create2CellPacket{} " + super.toString();
    }
}
