package model.cells.relaycells;

import model.payload.Payload;
import model.payload.relaypayload.DataRelayPayload;

public class DataRelayCell extends RelayCell {

    private final byte[] data;

    public DataRelayCell(int CIRC_ID, byte COMMAND, Payload payload) {
        super(CIRC_ID, COMMAND, payload);
        this.RELAY_PAYLOAD = new DataRelayPayload(this.getRelayPayload().getPayload());
        this.data = ((DataRelayPayload) RELAY_PAYLOAD).retrieveDataPayload(this.getLength());
    }

    public DataRelayCell(int CIR_ID, short STREAM_ID, DataRelayPayload PAYLOAD) {
        super(CIR_ID, RELAY_COMMAND_DATA, STREAM_ID, PAYLOAD);
        this.data = PAYLOAD.retrieveDataPayload(this.getLength());
    }


    public static DataRelayCell createFrom(byte[] data, int CIRC_ID, short STREAM_ID) {
        DataRelayPayload payload = new DataRelayPayload(data);
        return new DataRelayCell(CIRC_ID, STREAM_ID, payload);
    }

    public byte[] getData() {
        return data;
    }
}
