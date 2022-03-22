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
                //", DATA=" + ByteUtils.toHexString(payload) +
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

}
