package model.cell;

import utils.ByteUtils;

import java.nio.ByteBuffer;

public class CertPayload extends Payload {

    public final static int PAYLOAD_HEADER_LENGTH = 0x01;
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

    public static CertPayload createPayloadFrom(Certificate[] certificates) {
        int certificateCount = certificates.length;
        int bufferLength = PAYLOAD_HEADER_LENGTH;
        for(Certificate c : certificates) {bufferLength += (CERTIFICATE_HEADER_LENGTH + c.getLength()); }

        ByteBuffer pumpBuffer = ByteBuffer.allocate(bufferLength);
        pumpBuffer.put((byte) certificateCount);

        for(Certificate c : certificates) {
            pumpBuffer.put(c.getType());
            pumpBuffer.putShort((short) c.getLength());
            pumpBuffer.put(c.getCertificate());
        }

        return new CertPayload(pumpBuffer.array());
    }

}
