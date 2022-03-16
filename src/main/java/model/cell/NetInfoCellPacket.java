package model.cell;

public class NetInfoCellPacket extends CellPacket {

    private NetInfo netInfo;

    public NetInfoCellPacket(short CIRC_ID, byte COMMAND, byte[] payload) {
        super(CIRC_ID, COMMAND, payload);

        PAYLOAD = new NetInfoPayload(payload);
        initiateNetInfoRead();
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
