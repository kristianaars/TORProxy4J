package model.payload;

import model.cell.RelayCell;
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
        //byte[] buffer = CryptoUtils.getRandomlyPaddedBuffer(FIXED_PAYLOAD_SIZE);
        byte[] buffer = new byte[FIXED_PAYLOAD_SIZE];

        for(int i = 0; i < payload.length; i++) {
            buffer[i] = payload[i];
        }
        return buffer;
    }

    public int getDataLength() {
        return DATA_LENGTH;
    }
}
