package model.cell;

import model.Certificate;
import model.payload.CertPayload;

import java.util.Arrays;

public class CertCellPacket extends CellPacket {

    private Certificate[] certificates;

    public CertCellPacket(short CIRC_ID, byte COMMAND, Certificate[] certificates) {
        super(CIRC_ID, COMMAND, new byte[0]);

        PAYLOAD = CertPayload.createPayloadFrom(certificates);
        this.certificates = certificates;
    }

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
