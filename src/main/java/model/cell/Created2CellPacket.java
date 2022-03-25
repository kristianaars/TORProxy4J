package model.cell;

import model.ServerHandshakeResponse;
import model.payload.Created2Payload;
import model.payload.Extended2RelayPayload;
import utils.ByteUtils;

public class Created2CellPacket extends CellPacket {


    private final ServerHandshakeResponse handshakeResponse;

    public Created2CellPacket(int CIRC_ID, byte[] PAYLOAD) {
        super(CIRC_ID, CellPacket.CREATED2_COMMAND, PAYLOAD);

        this.PAYLOAD = new Created2Payload(this.PAYLOAD);

        handshakeResponse = retrieveServerHandshakeResponse();
    }

    private ServerHandshakeResponse retrieveServerHandshakeResponse() {;
        return new ServerHandshakeResponse(
                ((Created2Payload)this.PAYLOAD).retrieveServerPK(),
                ((Created2Payload)this.PAYLOAD).retrieveAuth()
        );
    }

    public ServerHandshakeResponse getHandshakeResponse() {
        return handshakeResponse;
    }

    @Override
    public String toString() {
        return "Created2CellPacket{" +
                "handshakeResponse=" + handshakeResponse +
                "} " + super.toString();
    }
}
