package model.payload;

public class DestroyPayload extends Payload {

    public DestroyPayload(Payload payload) {
        super(payload);
    }

    public DestroyPayload(byte[] payload) {
        super(payload);
    }

    public byte retrieveDestroyReason() {
        return payload[0];
    }
}
