package model.cells.relaycells;

import model.ServerHandshakeResponse;
import model.payload.relaypayload.Extended2RelayPayload;
import model.payload.Payload;

public class Extended2RelayCell extends RelayCell {

    private final ServerHandshakeResponse HANDSHAKE_RESPONSE;

    public Extended2RelayCell(int CIRC_ID, byte COMMAND, Payload PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);

        this.RELAY_PAYLOAD = new Extended2RelayPayload(RELAY_PAYLOAD);
        this.HANDSHAKE_RESPONSE = retrieveServerHandshakeResponse();
    }

    private ServerHandshakeResponse retrieveServerHandshakeResponse() {;
        return new ServerHandshakeResponse(
                ((Extended2RelayPayload)this.RELAY_PAYLOAD).retrieveServerPK(),
                ((Extended2RelayPayload)this.RELAY_PAYLOAD).retrieveAuth()
        );
    }

    public ServerHandshakeResponse getHandshakeResponse() {
        return HANDSHAKE_RESPONSE;
    }

    @Override
    public String toString() {
        return "Extended2RelayCell{" +
                "HANDSHAKE_RESPONSE=" + HANDSHAKE_RESPONSE +
                "} " + super.toString();
    }
}
