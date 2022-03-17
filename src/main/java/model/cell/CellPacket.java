package model.cell;

import model.payload.Payload;

import java.nio.ByteBuffer;

public class CellPacket {

    private final static int DEFAULT_HEADER_SIZE = 0x05;

    private final short CIRC_ID;
    private final byte COMMAND;
    protected Payload PAYLOAD;

    public CellPacket(short CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        this.CIRC_ID = CIRC_ID;
        this.COMMAND = COMMAND;
        this.PAYLOAD = new Payload(PAYLOAD);
    }

    public short getCIRC_ID() {
        return CIRC_ID;
    }

    public byte getCOMMAND() {
        return COMMAND;
    }

    public Payload getPayload() {
        return PAYLOAD;
    }

    public byte[] generateRawCellPacket() {
        int packetSize = DEFAULT_HEADER_SIZE + getPayload().getPayload().length;

        ByteBuffer pumpBuffer = ByteBuffer.allocate(packetSize);
        pumpBuffer.putShort(getCIRC_ID());
        pumpBuffer.put(getCOMMAND());

        if(getCOMMAND() != (byte) 0x08) {
            pumpBuffer.putShort((short) (getPayload().getLength() & 0xFFFF));
        }

        pumpBuffer.put(getPayload().getPayload());
        return pumpBuffer.array();
    }

    @Override
    public String toString() {
        return "CellPacket{" +
                "CIRC_ID=" + String.format("0x%02X", CIRC_ID) +
                ", COMMAND=" + String.format("0x%02X", COMMAND) +
                ", LENGTH=" + PAYLOAD.getLength() +
                //", PAYLOAD=" + PAYLOAD+
                '}';
    }
}

