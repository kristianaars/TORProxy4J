package model.cell;

import model.LinkSpecifier;
import model.payload.Extend2RelayPayload;
import model.payload.RelayPayload;
import utils.ByteUtils;

import java.util.Arrays;

public class Extend2RelayCell extends RelayEarlyCell {

    private final LinkSpecifier[] LSPEC;
    private final byte[] handshakeData;

    public Extend2RelayCell(int CIRC_ID, LinkSpecifier[] LSPEC, byte[] handshakeData) {
        super(CIRC_ID, RELAY_EARLY, (short) 0, new RelayPayload(new byte[0]));

        this.LSPEC = LSPEC;
        this.handshakeData = handshakeData;
        this.RELAY_PAYLOAD = Extend2RelayPayload.createRelayData(LSPEC, handshakeData);
        super.updatePayload();
    }

    @Override
    public String toString() {
        return "Extend2RelayCell{" +
                "LSPEC=" + Arrays.toString(LSPEC) +
                ", handshakeData=" + ByteUtils.toHexString(handshakeData) +
                "} " + super.toString();
    }
}
