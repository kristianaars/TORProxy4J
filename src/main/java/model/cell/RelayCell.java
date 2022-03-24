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

    private RelayPayload retrieveRelayPayload(Payload payload) {
        byte[] parentPayload = payload.getPayload();
        int length = ByteUtils.toUnsigned(ByteUtils.toShort(parentPayload[9], parentPayload[10]));
        return new RelayPayload(Arrays.copyOfRange(parentPayload, 11, 11 + length));
    }

    public RelayCell(int CIRC_ID, byte COMMAND, byte RELAY_COMMAND, short STREAM_ID, RelayPayload RELAY_PAYLOAD) {
        super(CIRC_ID, COMMAND, new byte[0]);

        this.RELAY_COMMAND = RELAY_COMMAND;
        this.RECOGNIZED = 0;
        this.DIGEST = 0;
        this.STREAM_ID = STREAM_ID;
        this.RELAY_PAYLOAD = RELAY_PAYLOAD;

        updatePayload();
    }

    protected void updatePayload() {
        this.PAYLOAD = buildCellPacketPayload(RELAY_COMMAND, RECOGNIZED, DIGEST, STREAM_ID, RELAY_PAYLOAD);
        this.PAYLOAD.setFixedSize(true);
    }

    public void setPayload(Payload p) {
        this.PAYLOAD = p;
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
    public static Payload buildCellPacketPayload(byte RELAY_COMMAND, short RECOGNIZED, int DIGEST, short STREAM_ID, RelayPayload RELAY_PAYLOAD) {
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

    public void calculateAndSetDigestValue(byte[] DF) {
        //Create digest value.
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            md.update(DF);
            md.update(super.PAYLOAD.getPayload());
            byte[] digestedMessage = md.digest();

            setDigestValue(Arrays.copyOfRange(digestedMessage, 0, 4));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void setDigestValue(byte[] b) {
        this.DIGEST = ByteUtils.toInt(b);
        updatePayload();
    }

    /**
     * Sets the relay-payload to the new provided payload.
     * Used to set new encrypted or decryptet payloads to the same cell.
     *
     * @param relayPayload New relay payload.
     */
    public void setRelayPayload(RelayPayload relayPayload) {
        this.RELAY_PAYLOAD = relayPayload;
        super.PAYLOAD = buildCellPacketPayload(RELAY_COMMAND, RECOGNIZED, DIGEST, STREAM_ID, RELAY_PAYLOAD);
        //Cell payload needs to be updated
        //TODO: Better way to handle PAYLOAD-Update?
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
                "RELAY_PAYLOAD=" + RELAY_PAYLOAD +
                ", RELAY_COMMAND=" + RELAY_COMMAND +
                ", RECOGNIZED=" + RECOGNIZED +
                ", STREAM_ID=" + STREAM_ID +
                ", DIGEST=" + DIGEST +
                "} " + super.toString();
    }
}
