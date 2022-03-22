package model.cell;

import model.payload.Payload;

import java.nio.ByteBuffer;

public class CellPacket {

    public static final byte VERSION_COMMAND = 0x07;
    public static final byte CERTS_COMMAND = (byte) 0x81;
    public static final byte AUTH_CHALLENGE_COMMAND = (byte) 0x82;
    public static final byte NETINFO_COMMAND = (byte) 0x08;
    public static final byte CREATE2_COMMAND = (byte) 0x0A;
    public static final byte CREATED2_COMMAND = (byte) 0x0B;
    public static final byte DESTROY_COMMAND = (byte) 0x04;

    private final static int FIXED_CELL_PACKET_SIZE = 514;
    private final static int VARIABLE_HEADER_SIZE = 0x07;

    private final int CIRC_ID;
    private final byte COMMAND;
    protected Payload PAYLOAD;

    public CellPacket(int CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        this.CIRC_ID = CIRC_ID;
        this.COMMAND = COMMAND;
        this.PAYLOAD = new Payload(PAYLOAD);
    }

    public int getCIRC_ID() {
        return CIRC_ID;
    }

    public byte getCOMMAND() {
        return COMMAND;
    }

    public Payload getPayload() {
        return PAYLOAD;
    }

    public byte[] generateRawCellPacket() {
        boolean fixedSize = getPayload().isFixedSize();
        int packetSize = getExpectedPacketSize();

        ByteBuffer pumpBuffer = ByteBuffer.allocate(packetSize);
        pumpBuffer.putInt(getCIRC_ID());
        pumpBuffer.put(getCOMMAND());

        System.out.println(fixedSize);

        if(!fixedSize) {
            pumpBuffer.putShort((short) (getPayload().getLength() & 0xFFFF));
        }

        pumpBuffer.put(getPayload().getPayload());
        return pumpBuffer.array();
    }

    public int getExpectedPacketSize() {
        boolean fixedSize = getPayload().isFixedSize();
        if(fixedSize) {
            return FIXED_CELL_PACKET_SIZE;
        } else {
            return VARIABLE_HEADER_SIZE + getPayload().getLength();
        }
    }

    public static boolean isFixedPacketCell(byte command) {
        return command == NETINFO_COMMAND || command == CREATE2_COMMAND || command == CREATED2_COMMAND || command == DESTROY_COMMAND;
    }

    @Override
    public String toString() {
        return "CellPacket{" +
                "CIRC_ID=" + String.format("0x%04X", CIRC_ID) +
                ", TOTAL_SIZE=" + getExpectedPacketSize() +
                ", COMMAND=" + String.format("0x%02X", COMMAND) +
                ", PAYLOAD=" + PAYLOAD+
                '}';
    }
}

