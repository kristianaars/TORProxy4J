package model.cells;

import model.payload.Payload;

import java.util.Objects;

public class CellPacket {

    public static final byte VERSION_COMMAND = 0x07;
    public static final byte CERTS_COMMAND = (byte) 0x81;
    public static final byte AUTH_CHALLENGE_COMMAND = (byte) 0x82;
    public static final byte NETINFO_COMMAND = (byte) 0x08;
    public static final byte CREATE2_COMMAND = (byte) 0x0A;
    public static final byte CREATED2_COMMAND = (byte) 0x0B;
    public static final byte DESTROY_COMMAND = (byte) 0x04;
    public static final byte RELAY_COMMAND = (byte) 0x03;
    public static final byte RELAY_EARLY_COMMAND = (byte) 0x09;

    public final static int FIXED_CELL_PACKET_SIZE = 514;
    public final static int HEADER_SIZE_V3 = 0x05;
    public final static int HEADER_SIZE_V4 = 0x07;

    private final int CIRC_ID;
    private final byte COMMAND;
    protected Payload PAYLOAD;

    public CellPacket(int CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        this.CIRC_ID = CIRC_ID;
        this.COMMAND = COMMAND;
        this.PAYLOAD = new Payload(PAYLOAD);
    }

    public CellPacket(int CIRC_ID, byte COMMAND, Payload PAYLOAD) {
        this.CIRC_ID = CIRC_ID;
        this.COMMAND = COMMAND;
        this.PAYLOAD = PAYLOAD;
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

    public int getExpectedPacketSize() {
        boolean fixedSize = getPayload().isFixedSize();
        if(fixedSize) {
            return FIXED_CELL_PACKET_SIZE;
        } else {
            return HEADER_SIZE_V3 + getPayload().getLength();
        }
    }

    public static boolean isFixedPacketCell(byte command) {
        return command == NETINFO_COMMAND ||
               command == CREATE2_COMMAND ||
               command == CREATED2_COMMAND ||
               command == DESTROY_COMMAND ||
               command == RELAY_COMMAND ||
               command == RELAY_EARLY_COMMAND;
    }

    @Override
    public String toString() {
        return "CellPacket{" +
                "CIRC_ID=" + String.format("0x%04X", CIRC_ID) +
                ", TOTAL_SIZE=" + getExpectedPacketSize() +
                ", COMMAND=" + String.format("0x%02X", COMMAND) +
                //", PAYLOAD=" + PAYLOAD +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellPacket packet = (CellPacket) o;

        if (CIRC_ID != packet.CIRC_ID) return false;
        if (COMMAND != packet.COMMAND) return false;
        return Objects.equals(PAYLOAD, packet.PAYLOAD);
    }

    @Override
    public int hashCode() {
        int result = CIRC_ID;
        result = 31 * result + (int) COMMAND;
        result = 31 * result + (PAYLOAD != null ? PAYLOAD.hashCode() : 0);
        return result;
    }

}

