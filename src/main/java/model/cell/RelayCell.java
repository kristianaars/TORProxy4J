package model.cell;

import model.payload.Payload;
import model.payload.RelayPayload;
import utils.ByteUtils;
import utils.CryptoUtils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class RelayCell extends CellPacket {

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

    public RelayCell(int CIRC_ID, byte[] PAYLOAD) {
        super(CIRC_ID, CellPacket.RELAY_COMMAND, PAYLOAD);

        this.RELAY_COMMAND =  PAYLOAD[0];;
        this.RECOGNIZED = ByteUtils.toShort(PAYLOAD[1], PAYLOAD[2]);;
        this.STREAM_ID = ByteUtils.toShort(PAYLOAD[3], PAYLOAD[4]);;
        this.DIGEST = ByteUtils.toInt(new byte[] { PAYLOAD[5], PAYLOAD[6], PAYLOAD[7], PAYLOAD[8]});;

        this.RELAY_PAYLOAD = retrieveRelayPayload(super.PAYLOAD);
    }

    public RelayCell(int CIRC_ID, Payload PAYLOAD) {
        super(CIRC_ID, CellPacket.RELAY_COMMAND, PAYLOAD);

        this.RELAY_COMMAND =  PAYLOAD.getPayload()[0];;
        this.RECOGNIZED = ByteUtils.toShort(PAYLOAD.getPayload()[1], PAYLOAD.getPayload()[2]);;
        this.STREAM_ID = ByteUtils.toShort(PAYLOAD.getPayload()[3], PAYLOAD.getPayload()[4]);;
        this.DIGEST = ByteUtils.toInt(new byte[] { PAYLOAD.getPayload()[5], PAYLOAD.getPayload()[6], PAYLOAD.getPayload()[7], PAYLOAD.getPayload()[8]});;

        this.RELAY_PAYLOAD = retrieveRelayPayload(super.PAYLOAD);
    }

    private RelayPayload retrieveRelayPayload(Payload payload) {
        byte[] parentPayload = payload.getPayload();

        int length = RelayPayload.FIXED_PAYLOAD_SIZE;
        if(this.RECOGNIZED == 0) {
            length = ByteUtils.toUnsigned(ByteUtils.toShort(parentPayload[9], parentPayload[10]));
        }

        return new RelayPayload(Arrays.copyOfRange(parentPayload, RELAY_HEADER_SIZE, RELAY_HEADER_SIZE + length));
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

    public int calculateDigestValue(byte[] D) {
        //Create digest value.
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            md.update(D);
            md.update(super.PAYLOAD.getPayload());
            byte[] digestedMessage = md.digest();

            return ByteUtils.toInt(Arrays.copyOfRange(digestedMessage, 0, 4));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void calculateAndSetDigestValue(byte[] D) {
        setDigestValue(calculateDigestValue(D));
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
                ", RELAY_PAYLOAD=" + RELAY_PAYLOAD +
                ", RELAY_COMMAND=" + RELAY_COMMAND +
                ", RECOGNIZED=" + RECOGNIZED +
                ", STREAM_ID=" + STREAM_ID +
                ", DIGEST=" + DIGEST +
                "} " + super.toString();
    }
}
