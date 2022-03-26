package model.cells;

public class AuthenticateCellPacket extends CellPacket {

    public AuthenticateCellPacket(short CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);
    }

}
