package model.cells;

import model.Certificate;
import model.payload.CertPayload;

import java.util.Arrays;

public class CertCellPacket extends CellPacket {

    private Certificate[] certificates;

    public CertCellPacket(int CIRC_ID, Certificate[] certificates) {
        super(CIRC_ID, CellPacket.CERTS_COMMAND, new byte[0]);

        PAYLOAD = CertPayload.createPayloadFrom(certificates);
        this.certificates = certificates;
    }

    public CertCellPacket(int CIRC_ID, byte[] payload) {
        super(CIRC_ID, CellPacket.CERTS_COMMAND, payload);

        PAYLOAD = new CertPayload(payload);
        initiateCertificateRead();
    }

    private void initiateCertificateRead() {
        certificates = getPayload().readAllCertificates();
    }

    public Certificate[] getCertificates() {
        return Arrays.copyOf(certificates, certificates.length);
    }

    public Certificate getCertificate(byte type) {
        for(Certificate c : certificates) { if(c.getType() == type) { return c;}}
        return null;
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
