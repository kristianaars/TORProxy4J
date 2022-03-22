package model.cell;

import model.payload.VersionPayload;

import java.util.Arrays;

public class VersionCellPacket extends CellPacket {

    private final short[] VERSIONS;

    public VersionCellPacket(int CIRC_ID, byte[] payload) {
        super(CIRC_ID, CellPacket.VERSION_COMMAND, payload);

        PAYLOAD = new VersionPayload(PAYLOAD);
        VERSIONS = ((VersionPayload) PAYLOAD).getVersions();
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

    @Override
    public String toString() {
        return "VersionCellPacket{" +
                "VERSIONS=" + Arrays.toString(VERSIONS) +
                "} " + super.toString();
    }
}
