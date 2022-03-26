package model.payload.relaypayload;

import model.cells.relaycells.RelayCell;
import model.payload.Payload;
import utils.CryptoUtils;

public class RelayPayload extends Payload {

    public static final int FIXED_PAYLOAD_SIZE = Payload.FIXED_PAYLOAD_SIZE - RelayCell.RELAY_HEADER_SIZE;

    /**
     * Length of the payload-data without padding
     */
    public final int DATA_LENGTH;

    public RelayPayload(byte[] payload) {
        super(new byte[0]);

        isFixedSize = true;
        this.DATA_LENGTH = payload.length;
        this.payload = padPayload(payload);
    }

    public byte[] padPayload(byte[] payload) {
        byte[] buffer = CryptoUtils.getRandomlyPaddedBuffer(FIXED_PAYLOAD_SIZE);
        //byte[] buffer = new byte[FIXED_PAYLOAD_SIZE];

        for(int i = 0; i < (payload.length + 2); i++) {
            if(i >= payload.length && i < buffer.length) {
                //Add two zeroes between payload and padding.
                buffer[i] = 0x00;
            } else if(i < buffer.length){
                buffer[i] = payload[i];
            }

        }
        return buffer;
    }

    public int getDataLength() {
        return DATA_LENGTH;
    }
}
