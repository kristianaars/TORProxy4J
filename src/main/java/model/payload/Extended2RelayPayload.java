package model.payload;

import crypto.NTorHandshake;
import model.ServerHandshakeResponse;

import java.util.Arrays;

public class Extended2RelayPayload extends RelayPayload {

    //Number of bytes used to represent size of the payload
    private final static int HLEN_LENGTH = 2;

    public Extended2RelayPayload(RelayPayload payload) {
        super(payload.payload);
    }

    public byte[] retrieveServerPK() {
        return Arrays.copyOfRange(payload, HLEN_LENGTH, HLEN_LENGTH + NTorHandshake.G_LENGTH);
    }

    public byte[] retrieveAuth() {
        return Arrays.copyOfRange(payload, HLEN_LENGTH + NTorHandshake.G_LENGTH, HLEN_LENGTH + NTorHandshake.G_LENGTH + NTorHandshake.H_LENGTH);
    }

}
