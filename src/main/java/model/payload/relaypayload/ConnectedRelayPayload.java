package model.payload.relaypayload;

import model.payload.Payload;

public class ConnectedRelayPayload extends RelayPayload {

    public ConnectedRelayPayload(Payload payload) {
        super(payload.getPayload());
    }

}
