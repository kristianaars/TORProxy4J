package model.payload;

import utils.ByteUtils;

import java.util.Arrays;

public class Payload {

    protected boolean isFixedSize;
    protected byte[] payload;
    public static final int FIXED_PAYLOAD_SIZE = 509;

    public Payload(Payload payload) {
        this.payload = Arrays.copyOf(payload.getPayload(), payload.getPayload().length);
    }

    public Payload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Payload{" +
                " LENGTH=" + payload.length +
                ", FIXED_SIZE=" + isFixedSize +
                ", DATA=" + ByteUtils.toHexString(payload) +
                '}';
    }

    public boolean isFixedSize() {
        return isFixedSize;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getLength() {
        return payload.length;
    }

    public void setFixedSize(boolean b) {
        isFixedSize = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payload payload1 = (Payload) o;

        if (isFixedSize != payload1.isFixedSize) return false;
        return Arrays.equals(payload, payload1.payload);
    }

    @Override
    public int hashCode() {
        int result = (isFixedSize ? 1 : 0);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
