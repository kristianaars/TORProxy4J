package model.payload;

public class RelayData extends Payload {

    public RelayData(byte[] payload) {
        super(payload);

        isFixedSize = false;
    }

}
