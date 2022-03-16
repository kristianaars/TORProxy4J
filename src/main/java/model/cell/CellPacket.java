package model.cell;

import java.util.Arrays;

public class CellPacket {

    private final short CIRC_ID;
    private final byte COMMAND;
    private final int LENGTH;
    protected Payload PAYLOAD;

    public CellPacket(short CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        this.CIRC_ID = CIRC_ID;
        this.COMMAND = COMMAND;
        this.LENGTH = PAYLOAD.length;
        this.PAYLOAD = new Payload(PAYLOAD);
    }

    public short getCIRC_ID() {
        return CIRC_ID;
    }

    public byte getCOMMAND() {
        return COMMAND;
    }

    public int getLENGTH() {
        return LENGTH;
    }

    public Payload getPAYLOAD() {
        return PAYLOAD;
    }

    @Override
    public String toString() {
        return "CellPacket{" +
                "CIRC_ID=" + String.format("0x%02X", CIRC_ID) +
                ", COMMAND=" + String.format("0x%02X", COMMAND) +
                ", LENGTH=" + LENGTH +
                ", PAYLOAD=" + PAYLOAD+
                '}';
    }
}

