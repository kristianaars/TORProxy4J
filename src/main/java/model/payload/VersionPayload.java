package model.payload;

import utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class VersionPayload extends Payload {

    public VersionPayload(Payload payload) {
        super(payload);
    }

    public VersionPayload(byte[] payload) {
        super(payload);
    }

    public short[] getVersions() {
        short[] versions = new short[payload.length/2];
        for (int i = 0; i < payload.length; i+=2) {
            versions[i/2] = ByteUtils.toShort(payload[i], payload[i+1]);
        }

        return versions;
    }

    public static VersionPayload createPayloadFrom(short[] versions) {
        ByteBuffer pumpBuffer = ByteBuffer.allocate(versions.length * 2);

        for (short v : versions) {
            pumpBuffer.putShort(v);
        }

        return new VersionPayload(pumpBuffer.array());
    }
}
