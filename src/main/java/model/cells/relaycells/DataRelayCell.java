package model.cells.relaycells;

import model.cells.CellPacket;
import model.payload.Payload;
import model.payload.relaypayload.RelayPayload;

public class DataRelayCell extends RelayCell {

    public DataRelayCell(int CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        super(CIRC_ID, CellPacket.RELAY_COMMAND, PAYLOAD);
    }

    public DataRelayCell(int CIR_ID, short STREAM_ID, RelayPayload PAYLOAD) {
        super(CIR_ID, RELAY_COMMAND_DATA, STREAM_ID, PAYLOAD);
    }

    public static DataRelayCell createFrom(byte[] data, int CIRC_ID, short STREAM_ID) {
        RelayPayload payload = new RelayPayload(data);
        return new DataRelayCell(CIRC_ID, STREAM_ID, payload);
    }
}
