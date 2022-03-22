package model.payload;

import crypto.NTorHandshake;

import java.util.Arrays;

public class Created2Payload extends Payload {

    //Number of bytes used to represent size of the payload
    private final static int HLEN_LENGTH = 2;

    public Created2Payload(Payload payload) {
        super(payload);

        this.isFixedSize = true;
    }

    public Created2Payload(byte[] payload) {
        super(payload);

        this.isFixedSize = true;
    }

    public byte[] retrieveServerPK() {
        return Arrays.copyOfRange(payload, HLEN_LENGTH, HLEN_LENGTH + NTorHandshake.G_LENGTH);
    }

    public byte[] retrieveAuth() {
        return Arrays.copyOfRange(payload, HLEN_LENGTH + NTorHandshake.G_LENGTH, HLEN_LENGTH + NTorHandshake.G_LENGTH + NTorHandshake.H_LENGTH);
    }

}
