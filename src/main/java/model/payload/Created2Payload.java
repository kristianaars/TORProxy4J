package model.payload;

public class Created2Payload extends Payload {

    public Created2Payload(Payload payload) {
        super(payload);

        this.isFixedSize = true;
    }

    public Created2Payload(byte[] payload) {
        super(payload);

        this.isFixedSize = true;
    }

}
