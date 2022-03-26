package model.cells.relaycells;

import model.cells.CellPacket;
import model.payload.Payload;
import model.payload.relaypayload.RelayPayload;
import utils.ByteUtils;
import utils.CryptoUtils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class RelayCell extends CellPacket {

    /**
     *    The relay commands are:
     *
     *          1 -- RELAY_BEGIN     [forward]
     *          2 -- RELAY_DATA      [forward or backward]
     *          3 -- RELAY_END       [forward or backward]
     *          4 -- RELAY_CONNECTED [backward]
     *          5 -- RELAY_SENDME    [forward or backward] [sometimes control]
     *          6 -- RELAY_EXTEND    [forward]             [control]
     *          7 -- RELAY_EXTENDED  [backward]            [control]
     *          8 -- RELAY_TRUNCATE  [forward]             [control]
     *          9 -- RELAY_TRUNCATED [backward]            [control]
     *         10 -- RELAY_DROP      [forward or backward] [control]
     *         11 -- RELAY_RESOLVE   [forward]
     *         12 -- RELAY_RESOLVED  [backward]
     *         13 -- RELAY_BEGIN_DIR [forward]
     *         14 -- RELAY_EXTEND2   [forward]             [control]
     *         15 -- RELAY_EXTENDED2 [backward]            [control]
     */

    public static final byte RELAY_COMMAND_BEGIN = 0x01;
    public static final byte RELAY_COMMAND_DATA = 0x02;
    public static final byte RELAY_COMMAND_END = 0x03;
    public static final byte RELAY_COMMAND_CONNECTED = 0x04;
    public static final byte RELAY_COMMAND_EXTEND2 = 0x0E;
    public static final byte RELAY_COMMAND_EXTENDED2 = 0x0F;

    public static final int RELAY_HEADER_SIZE = 11;


    protected RelayPayload RELAY_PAYLOAD;
    private final byte RELAY_COMMAND;

    /**
     *    The 'recognized' field is used as a simple indication that the cell
     *    is still encrypted. It is an optimization to avoid calculating
     *    expensive digests for every cell. When sending cells, the unencrypted
     *    'recognized' MUST be set to zero.
     */
    private final short RECOGNIZED;
    private final short STREAM_ID;
    private int DIGEST;

    public RelayCell(int CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);

        this.RELAY_COMMAND =  PAYLOAD[0];;
        this.RECOGNIZED = ByteUtils.toShort(PAYLOAD[1], PAYLOAD[2]);;
        this.STREAM_ID = ByteUtils.toShort(PAYLOAD[3], PAYLOAD[4]);;
        this.DIGEST = ByteUtils.toInt(new byte[] { PAYLOAD[5], PAYLOAD[6], PAYLOAD[7], PAYLOAD[8]});;

        this.RELAY_PAYLOAD = retrieveRelayPayload(super.PAYLOAD);
    }

    public RelayCell(int CIRC_ID, byte COMMAND, Payload PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);

        this.RELAY_COMMAND =  PAYLOAD.getPayload()[0];;
        this.RECOGNIZED = ByteUtils.toShort(PAYLOAD.getPayload()[1], PAYLOAD.getPayload()[2]);;
        this.STREAM_ID = ByteUtils.toShort(PAYLOAD.getPayload()[3], PAYLOAD.getPayload()[4]);;
        this.DIGEST = ByteUtils.toInt(new byte[] { PAYLOAD.getPayload()[5], PAYLOAD.getPayload()[6], PAYLOAD.getPayload()[7], PAYLOAD.getPayload()[8]});;

        this.RELAY_PAYLOAD = retrieveRelayPayload(super.PAYLOAD);
    }

    public RelayCell(int CIRC_ID, byte COMMAND, byte RELAY_COMMAND, short STREAM_ID, RelayPayload RELAY_PAYLOAD) {
        super(CIRC_ID, COMMAND, new byte[0]);

        this.RELAY_COMMAND = RELAY_COMMAND;
        this.RECOGNIZED = 0;
        this.DIGEST = 0;
        this.STREAM_ID = STREAM_ID;
        this.RELAY_PAYLOAD = RELAY_PAYLOAD;

        this.PAYLOAD = buildCellPacketPayload(RELAY_COMMAND, RECOGNIZED, DIGEST, STREAM_ID, RELAY_PAYLOAD);
        this.PAYLOAD.setFixedSize(true);
    }

    public RelayCell(int CIRC_ID, byte RELAY_COMMAND, short STREAM_ID, RelayPayload RELAY_PAYLOAD) {
        super(CIRC_ID, CellPacket.RELAY_COMMAND, new byte[0]);

        this.RELAY_COMMAND = RELAY_COMMAND;
        this.RECOGNIZED = 0;
        this.DIGEST = 0;
        this.STREAM_ID = STREAM_ID;
        this.RELAY_PAYLOAD = RELAY_PAYLOAD;

        this.PAYLOAD = buildCellPacketPayload(RELAY_COMMAND, RECOGNIZED, DIGEST, STREAM_ID, RELAY_PAYLOAD);
        this.PAYLOAD.setFixedSize(true);
    }

    private RelayPayload retrieveRelayPayload(Payload payload) {
        byte[] parentPayload = payload.getPayload();

        int length = RelayPayload.FIXED_PAYLOAD_SIZE;
        if(this.RECOGNIZED == 0) {
            length = ByteUtils.toUnsigned(ByteUtils.toShort(parentPayload[9], parentPayload[10]));
        }

        return new RelayPayload(Arrays.copyOfRange(parentPayload, RELAY_HEADER_SIZE, RELAY_HEADER_SIZE + length));
    }

    /**
     The payload of each unencrypted RELAY cell consists of:

     Relay command           [1 byte]
     'Recognized'            [2 bytes]
     StreamID                [2 bytes]
     Digest                  [4 bytes] (4 first bytes of SHA-1 digest of Salt DF + message with Digest set to 0)
     Length                  [2 bytes]
     Data (RelayPayload)     [Length bytes]
     Padding                 [PAYLOAD_LEN - 11 - Length bytes]

     Padding must be random.
     */
    protected Payload buildCellPacketPayload(byte RELAY_COMMAND, short RECOGNIZED, int DIGEST, short STREAM_ID, RelayPayload RELAY_PAYLOAD) {
        ByteBuffer pumpBuffer = ByteBuffer.wrap(CryptoUtils.getRandomlyPaddedBuffer(Payload.FIXED_PAYLOAD_SIZE));

        pumpBuffer.put(RELAY_COMMAND);
        pumpBuffer.putShort(RECOGNIZED);
        pumpBuffer.putShort(STREAM_ID);
        pumpBuffer.putInt(DIGEST); //Digest is initially sat to zero.
        pumpBuffer.putShort((short) RELAY_PAYLOAD.getDataLength());
        pumpBuffer.put(RELAY_PAYLOAD.getPayload());

        Payload payload = new Payload(pumpBuffer.array());
        payload.setFixedSize(true);
        return payload;
    }

    public void setDigestValue(int d) {
        this.DIGEST = d;
        byte[] b = ByteUtils.toByteArray(d);

        int DIGEST_VALUE_START = 1 + 2 + 2; //REL_COMMAND [1 B] + RECOGNIZED [2 B] + STREAMID [2 B]
        byte[] payload = PAYLOAD.getPayload();
        for(int i = DIGEST_VALUE_START; i < DIGEST_VALUE_START + b.length; i++) {
            payload[i] = b[i - DIGEST_VALUE_START];
        }
    }

    public boolean isEncrypted() {
        return getRECOGNIZED() != 0;
    }

    public RelayPayload getRelayPayload() {
        return RELAY_PAYLOAD;
    }

    public byte getRELAY_COMMAND() {
        return RELAY_COMMAND;
    }

    public short getRECOGNIZED() {
        return RECOGNIZED;
    }

    public short getSTREAM_ID() {
        return STREAM_ID;
    }

    public int getDIGEST() {
        return DIGEST;
    }

    @Override
    public String toString() {
        return "RelayCell{" +
                "IS_ENCRYPTED=" + isEncrypted() +
                ", RELAY_COMMAND=" + RELAY_COMMAND +
                ", RECOGNIZED=" + RECOGNIZED +
                ", STREAM_ID=" + STREAM_ID +
                ", DIGEST=" + DIGEST +
                ", RELAY_PAYLOAD=" + RELAY_PAYLOAD +
                "} " + super.toString();
    }
}
