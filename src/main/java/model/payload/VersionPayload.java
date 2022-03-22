package model.payload;

import utils.ByteUtils;

import java.util.Arrays;

public class VersionPayload extends Payload {

    public VersionPayload(Payload payload) {
        super(payload);
    }

    public short[] getVersions() {
        short[] versions = new short[payload.length/2];
        for (int i = 0; i < payload.length; i+=2) {
            versions[i/2] = ByteUtils.toShort(payload[i], payload[i+1]);
        }

        return versions;
    }
}
