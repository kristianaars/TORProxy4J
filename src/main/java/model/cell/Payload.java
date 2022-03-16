package model.cell;

import utils.ByteUtils;

import java.util.Arrays;

public class Payload {

    protected byte[] payload;

    public Payload(Payload payload) {
        this.payload = Arrays.copyOf(payload.getPayload(), payload.getPayload().length);
    }

    public Payload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "payload=" + ByteUtils.toString(payload) +
                '}';
    }

    private byte[] getPayload() {
        return payload;
    }

    public int getLength() {
        return payload.length;
    }
}
