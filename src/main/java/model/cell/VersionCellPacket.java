package model.cell;

import model.payload.VersionPayload;
import utils.ByteUtils;

import java.util.Arrays;

public class VersionCellPacket extends CellPacket {

    private final short[] VERSIONS;

    public VersionCellPacket(int CIRC_ID, byte[] payload) {
        super(CIRC_ID, CellPacket.VERSION_COMMAND, payload);

        PAYLOAD = new VersionPayload(PAYLOAD);
        VERSIONS = ((VersionPayload) PAYLOAD).getVersions();
    }

    public VersionCellPacket(int CIRC_ID, short[] versions) {
        super(CIRC_ID, CellPacket.VERSION_COMMAND, new byte[0]);

        PAYLOAD = VersionPayload.createPayloadFrom(versions);
        VERSIONS = versions;
    }

    public short[] getVERSIONS() {
        return VERSIONS;
    }

    public boolean supportsVersion(short version) {
        for(short v : getVERSIONS()) {
            if(v == version) return true;
        }
        return false;
    }

    public short highestCommonVersion(VersionCellPacket other) {
        short[] commonVersions = new short[Integer.max(other.getVERSIONS().length, this.getVERSIONS().length)];

        int i = 0;
        for(short v_o : other.getVERSIONS()) {
            for(short v_t : this.getVERSIONS()) {
                if(v_o == v_t) {
                    commonVersions[i++] = v_o;
                    break;
                }
            }
        }

        return ByteUtils.largest(Arrays.copyOfRange(commonVersions, 0, i + 1));
    }

    @Override
    public String toString() {
        return "VersionCellPacket{" +
                "VERSIONS=" + Arrays.toString(VERSIONS) +
                "} " + super.toString();
    }
}
