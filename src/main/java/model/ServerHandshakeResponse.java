package model;

import utils.ByteUtils;

public class ServerHandshakeResponse {

    private final byte[] SERVER_PK;
    private final byte[] AUTH;

    public ServerHandshakeResponse(byte[] SERVER_PK, byte[] AUTH) {
        this.SERVER_PK = SERVER_PK;
        this.AUTH = AUTH;
    }

    /**
     * Get the provided server public key
     * @return Server public key as represented in the cell-packet
     */
    public byte[] getServerPK() {
        return SERVER_PK;
    }

    /**
     * Get the provided authentication key
     * @return Authentication key as provided by the server
     */
    public byte[] getAuth() {
        return AUTH;
    }

    @Override
    public String toString() {
        return "ServerHandshakeResponse{" +
                "SERVER_PK=" + ByteUtils.toHexString(SERVER_PK) +
                ", AUTH=" + ByteUtils.toHexString(AUTH) +
                '}';
    }
}
