package model.payload;

import model.cell.RelayCell;
import utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RelayPayload extends Payload {

    public static final int FIXED_PAYLOAD_SIZE = Payload.FIXED_PAYLOAD_SIZE - RelayCell.RELAY_HEADER_SIZE;

    public RelayPayload(Payload payload) {
        super(payload);
        this.isFixedSize = true;
    }

    public RelayPayload() {
        super(new byte[FIXED_PAYLOAD_SIZE]);
        this.isFixedSize = true;
    }

    public RelayPayload(byte[] payload) {
        super(payload);
        this.isFixedSize = true;
    }



    /*
      The payload of each unencrypted RELAY cell consists of:

         Relay command           [1 byte]
         'Recognized'            [2 bytes]
         StreamID                [2 bytes]
         Digest                  [4 bytes]
         Length                  [2 bytes]
         Data                    [Length bytes]
         Padding                 [PAYLOAD_LEN - 11 - Length bytes]

     */

    public static RelayPayload createPayload(byte RELAY_COMMAND, short RECOGNIZED, short STREAM_ID, int DIGEST, RelayData DATA) {
        ByteBuffer pumpBuffer = ByteBuffer.allocate(RelayPayload.FIXED_PAYLOAD_SIZE);
        pumpBuffer.put(RELAY_COMMAND);
        pumpBuffer.putShort(RECOGNIZED);
        pumpBuffer.putShort(STREAM_ID);
        pumpBuffer.putInt(DIGEST);
        pumpBuffer.putShort((short) DATA.getLength());
        pumpBuffer.put(DATA.getPayload());
        return new RelayPayload(pumpBuffer.array());

        //TODO: Padding SHOULD be random bytes
    }

    public byte getRelayCommand() {
        return payload[0];
    }

    public short getRecognized() {
        return ByteUtils.toShort(payload[1], payload[2]);
    }

    public short getStreamID() {
        return ByteUtils.toShort(payload[3], payload[4]);
    }

    public int getDigest() {
        return ByteUtils.toInt(new byte[] { payload[5], payload[6], payload[7], payload[8]});
    }

    public byte[] getData() {
        int length = ByteUtils.toUnsigned(ByteUtils.toShort(payload[9], payload[10]));
        return Arrays.copyOfRange(payload, 11, 11 + length);
    }
}
