package model;

import utils.ByteUtils;

import java.util.Arrays;

public class AuthChallenge {

    private final byte[] CHALLENGE;
    private final short[] METHODS;

    public AuthChallenge(byte[] CHALLENGE, short[] METHODS) {
        this.CHALLENGE = CHALLENGE;
        this.METHODS = METHODS;
    }

    @Override
    public String toString() {
        return "AuthChallenge{" +
                "CHALLENGE=" + ByteUtils.toString(CHALLENGE) +
                ", METHODS=" + Arrays.toString(METHODS) +
                '}';
    }
}
