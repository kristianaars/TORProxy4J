package model.cell;

import model.payload.RelayData;
import model.payload.RelayPayload;

import java.nio.ByteBuffer;

public class RelayCell extends CellPacket {

    public static final int RELAY_HEADER_SIZE = 11;

    private final RelayPayload PAYLOAD;

    private final byte RELAY_COMMAND;

    /**
     *    The 'recognized' field is used as a simple indication that the cell
     *    is still encrypted. It is an optimization to avoid calculating
     *    expensive digests for every cell. When sending cells, the unencrypted
     *    'recognized' MUST be set to zero.
     */
    private final short RECOGNIZED;
    private final short STREAM_ID;
    private final int DIGEST;
    protected RelayData DATA;

    public RelayCell(int CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);

        this.PAYLOAD = new RelayPayload(PAYLOAD);
        super.PAYLOAD = this.PAYLOAD;

        this.RELAY_COMMAND = this.PAYLOAD.getRelayCommand();
        this.RECOGNIZED = this.PAYLOAD.getRecognized();
        this.STREAM_ID = this.PAYLOAD.getStreamID();
        this.DIGEST = this.PAYLOAD.getDigest();
        this.DATA = new RelayData(this.PAYLOAD.getData());
    }

    public RelayCell(int CIRC_ID, byte COMMAND, byte RELAY_COMMAND, short STREAM_ID, int DIGEST, RelayData DATA) {
        super(CIRC_ID, COMMAND, new byte[0]);

        this.RELAY_COMMAND = RELAY_COMMAND;
        this.RECOGNIZED = 0;
        this.STREAM_ID = STREAM_ID;
        this.DIGEST = DIGEST;
        this.DATA = DATA;

        this.PAYLOAD = RelayPayload.createPayload(RELAY_COMMAND, RECOGNIZED, STREAM_ID, DIGEST, DATA);
        super.PAYLOAD = this.PAYLOAD;
    }

    public RelayPayload getPAYLOAD() {
        return PAYLOAD;
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

    public RelayData getDATA() {
        return DATA;
    }

    @Override
    public String toString() {
        return "RelayCell{" +
                "PAYLOAD=" + PAYLOAD +
                ", RELAY_COMMAND=" + RELAY_COMMAND +
                ", RECOGNIZED=" + RECOGNIZED +
                ", STREAM_ID=" + STREAM_ID +
                ", DIGEST=" + DIGEST +
                ", DATA=" + DATA +
                "} " + super.toString();
    }
}
