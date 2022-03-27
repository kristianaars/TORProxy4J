package model.payload.relaypayload;

import java.util.Arrays;

public class DataRelayPayload extends RelayPayload {

    public DataRelayPayload(byte[] payload) {
        super(payload);
    }

    public byte[] retrieveDataPayload(short length) {
        return Arrays.copyOfRange(payload, 0, length);
    }

}
