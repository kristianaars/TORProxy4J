package model.cell;

import model.LinkSpecifier;
import model.LinkSpecifierGenerator;
import model.payload.Extend2RelayData;
import model.payload.RelayData;
import model.payload.RelayPayload;
import utils.ByteUtils;

import java.util.Arrays;

public class Extend2RelayCell extends RelayEarlyCell {

    private LinkSpecifier[] LSPEC;
    private byte[] handshakeData;

    public Extend2RelayCell(int CIRC_ID , int DIGEST, LinkSpecifier[] LSPEC, byte[] handshakeData) {
        super(CIRC_ID, RELAY_EARLY, (short) 0, DIGEST, new RelayData(new byte[0]));

        this.LSPEC = LSPEC;
        this.handshakeData = handshakeData;
        this.DATA = Extend2RelayData.createRelayData(LSPEC, handshakeData);

    }

    @Override
    public Extend2RelayData getDATA() {
        return (Extend2RelayData) super.getDATA();
    }

    @Override
    public String toString() {
        return "Extend2RelayCell{" +
                "LSPEC=" + Arrays.toString(LSPEC) +
                ", handshakeData=" + ByteUtils.toHexString(handshakeData) +
                "} " + super.toString();
    }
}
