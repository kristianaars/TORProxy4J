package model.cells.relaycells;

import model.LinkSpecifier;
import model.payload.relaypayload.Extend2RelayPayload;
import model.payload.relaypayload.RelayPayload;
import utils.ByteUtils;

import java.util.Arrays;

public class Extend2RelayCell extends RelayEarlyCell {

    private final LinkSpecifier[] LSPEC;
    private final byte[] handshakeData;

    public Extend2RelayCell(int CIRC_ID, LinkSpecifier[] LSPEC, byte[] handshakeData) {
        super(CIRC_ID, RelayCell.RELAY_COMMAND_EXTEND2, (short) 0, new RelayPayload(new byte[0]));

        this.LSPEC = LSPEC;
        this.handshakeData = handshakeData;
        this.RELAY_PAYLOAD = Extend2RelayPayload.createRelayData(LSPEC, handshakeData);

        this.PAYLOAD = buildCellPacketPayload(this.getRELAY_COMMAND(), this.getRECOGNIZED(), this.getDIGEST(), this.getSTREAM_ID(), RELAY_PAYLOAD);
        this.PAYLOAD.setFixedSize(true);
    }

    @Override
    public String toString() {
        return "Extend2RelayCell{" +
                "LSPEC=" + Arrays.toString(LSPEC) +
                ", handshakeDataLength=" + handshakeData.length +
                "} " + super.toString();
    }
}
