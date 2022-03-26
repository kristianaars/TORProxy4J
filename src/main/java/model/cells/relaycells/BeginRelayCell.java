package model.cells.relaycells;

import model.payload.relaypayload.BeginRelayPayload;
import model.payload.relaypayload.RelayPayload;

public class BeginRelayCell extends RelayCell {

    private final String hostname;
    private final int port;

    public BeginRelayCell(int CIRC_ID, byte RELAY_COMMAND, short STREAM_ID, BeginRelayPayload RELAY_PAYLOAD) {
        super(CIRC_ID, RELAY_COMMAND, STREAM_ID, RELAY_PAYLOAD);

        this.hostname = RELAY_PAYLOAD.retrieveAddress();
        this.port = RELAY_PAYLOAD.retrievePort();
    }

    public static BeginRelayCell generateBeginRelayCell(int CIRC_ID, short STREAM_ID, String address, int port) {
        BeginRelayPayload relayPayload = BeginRelayPayload.generateBeginRelayPayload(address, port);
        return new BeginRelayCell(CIRC_ID, RelayCell.RELAY_COMMAND_BEGIN, STREAM_ID, relayPayload);
    }

    @Override
    public String toString() {
        return "BeginRelayCell{" +
                "hostname='" + hostname + '\'' +
                ", port=" + port +
                "} " + super.toString();
    }
}
