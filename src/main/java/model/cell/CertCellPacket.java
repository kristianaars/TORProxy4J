package model.cell;

import utils.ByteUtils;

import java.util.Arrays;

public class CertCellPacket extends CellPacket {

    private Certificate[] certificates;

    public CertCellPacket(short CIRC_ID, byte COMMAND, byte[] payload) {
        super(CIRC_ID, COMMAND, payload);

        PAYLOAD = new CertPayload(payload);
        initiateCertificateRead();
    }

    private void initiateCertificateRead() {
        certificates = getPayload().readAllCertificates();
    }

    public Certificate[] getCertificates() {
        return Arrays.copyOf(certificates, certificates.length);
    }

    public CertPayload getPayload() {
        return (CertPayload) PAYLOAD;
    }

    @Override
    public String toString() {
        return "CertCellPacket{" +
                "certificates=" + Arrays.toString(certificates) +
                "} " + super.toString();
    }
}
