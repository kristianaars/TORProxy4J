package model;

import utils.ByteUtils;

import java.util.Arrays;

public class Certificate {

    private final byte type;
    private final byte[] certificate;
    private final int length;

    public Certificate(byte type, byte[] certificate) {
        this.type = type;
        this.certificate = certificate;
        this.length = certificate.length;
    }

    public byte getType() {
        return type;
    }

    public byte[] getCertificate() {
        return Arrays.copyOf(certificate, certificate.length);
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "type=" + type +
                ", certificate=[" + ByteUtils.toString(certificate) + "]" +
                ", length=" + length +
                '}';
    }
}
