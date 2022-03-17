package model;

import java.util.Arrays;

public class Create2 {

    public static final short HANDSHAKE_TYPE_TAP = 0x0000;
    public static final short HANDSHAKE_TYPE_NTOR = 0x0002;

    private final short handshakeType;
    private final short handshakeDataLength;
    private final byte[] handshakeData;

    public Create2(short handshakeType, byte[] handshakeData) {
        this.handshakeType = handshakeType;
        this.handshakeDataLength = (short) handshakeData.length;
        this.handshakeData = handshakeData;
    }

    public short getHandshakeType() {
        return handshakeType;
    }

    public short getHandshakeDataLength() {
        return handshakeDataLength;
    }

    public byte[] getHandshakeData() {
        return Arrays.copyOf(handshakeData, handshakeData.length);
    }
}
