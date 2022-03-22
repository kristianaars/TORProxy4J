package model.payload;

import utils.ByteUtils;

import java.nio.ByteBuffer;

public class Create2Payload extends Payload {

    //TODO: Denne bør flyttes til et mer logisk sted...
    public final static short HTYPE_NTOR = 0x0002;

    public Create2Payload(Payload payload) {
        super(payload);

        this.isFixedSize = true;
    }

    public Create2Payload(byte[] payload) {
        super(payload);

        this.isFixedSize = true;
    }

    /**
     *    A CREATE2 cell contains:
     *
     *    HTYPE     (Client Handshake Type)     [2 bytes]
     *    HLEN      (Client Handshake Data Len) [2 bytes]
     *    HDATA     (Client Handshake Data)     [HLEN bytes]
     *
     */
    public static Create2Payload generateCreate2Payload(short HTYPE, byte[] HDATA) {
        short HLEN = (short) HDATA.length;
        ByteBuffer pumpBuffer = ByteBuffer.allocate(Payload.FIXED_PAYLOAD_SIZE);
        pumpBuffer.putShort(HTYPE);
        pumpBuffer.putShort(HLEN);
        pumpBuffer.put(HDATA);

        return new Create2Payload(pumpBuffer.array());
    }

}
