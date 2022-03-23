package model.payload;

public class Create2Payload extends Payload {

    public Create2Payload(Payload payload) {
        super(payload);

        this.isFixedSize = true;
    }

    public Create2Payload(byte[] payload) {
        super(payload);

        this.isFixedSize = true;
    }

}
