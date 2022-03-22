package model.cell;

import model.payload.Created2Payload;
import utils.ByteUtils;

public class Created2CellPacket extends CellPacket {

    private final byte[] SERVER_PK;
    private final byte[] AUTH;

    public Created2CellPacket(int CIRC_ID, byte[] PAYLOAD) {
        super(CIRC_ID, CellPacket.CREATED2_COMMAND, PAYLOAD);

        this.PAYLOAD = new Created2Payload(this.PAYLOAD);

        SERVER_PK = ((Created2Payload)this.PAYLOAD).retrieveServerPK();
        AUTH = ((Created2Payload)this.PAYLOAD).retrieveAuth();
    }

    @Override
    public String toString() {
        return "Created2CellPacket{" +
                "SERVER_PK=" + ByteUtils.toHexString(SERVER_PK) +
                ", AUTH=" + ByteUtils.toHexString(AUTH) +
                "} " + super.toString();
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
}
