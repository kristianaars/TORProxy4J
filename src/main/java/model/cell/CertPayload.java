package model.cell;

import utils.ByteUtils;

public class CertPayload extends Payload {

    public final static int CERTIFICATE_HEADER_LENGTH = 0x03;

    public CertPayload(byte[] payload) {
        super(payload);
    }

    public Certificate[] readAllCertificates() {
        int certificateCount = getCertificateCount();
        int bufferIndex = 1;

        Certificate[] certificates = new Certificate[certificateCount];

        for(int i = 0; i < certificateCount; i++) {
            Certificate cert = getCertificate(bufferIndex);
            certificates[i] = cert;

            bufferIndex += CERTIFICATE_HEADER_LENGTH + cert.getLength();
        }

        return certificates;
    }

    private byte getCertificateCount() {
        return payload[0];
    }

    public Certificate getCertificate(int startIndex) {
        byte certType = payload[startIndex];
        int certLength = ByteUtils.toUnsigned(ByteUtils.toShort(payload[startIndex + 1], payload[startIndex + 2]));

        //1 byte for cert-type, two bytes for certLength
        int startPos = startIndex + CERTIFICATE_HEADER_LENGTH;

        byte[] certBuffer = new byte[certLength];
        for(int i = 0; i < certLength; i++) {
            certBuffer[i] = payload[i + startPos];
        }

        return new Certificate(certType, certBuffer);
    }

}
