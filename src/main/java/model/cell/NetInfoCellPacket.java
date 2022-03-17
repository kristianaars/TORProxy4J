package model.cell;

import exceptions.PayloadSizeNotFixedException;
import model.NetInfo;
import model.payload.NetInfoPayload;

public class NetInfoCellPacket extends CellPacket {

    private NetInfo netInfo;

    public NetInfoCellPacket(short CIRC_ID, byte COMMAND, byte[] payload) throws PayloadSizeNotFixedException {
        super(CIRC_ID, COMMAND, payload);

        PAYLOAD = new NetInfoPayload(payload);
        initiateNetInfoRead();
    }

    public NetInfoCellPacket(short circ_id, byte command, NetInfo netInfo) {
        super(circ_id, command, new byte[0]);

        PAYLOAD = NetInfoPayload.createPayloadFrom(netInfo);
        this.netInfo = netInfo;
    }

    private void initiateNetInfoRead() {
        netInfo = getPayload().readNetInfoData();
    }

    public NetInfoPayload getPayload() {
        return (NetInfoPayload) PAYLOAD;
    }

    public NetInfo getNetInfo() {
        return netInfo;
    }

    @Override
    public String toString() {
        return "NetInfoCellPacket{" +
                "netInfo=" + netInfo +
                "} " + super.toString();
    }
}


