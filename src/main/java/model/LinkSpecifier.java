package model;

import utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *    Link specifiers describe the next node in the circuit and how to
 *    connect to it. Recognized specifiers are:
 *
 *       [00] TLS-over-TCP, IPv4 address
 *            A four-byte IPv4 address plus two-byte ORPort
 *       [01] TLS-over-TCP, IPv6 address
 *            A sixteen-byte IPv6 address plus two-byte ORPort
 *       [02] Legacy identity
 *            A 20-byte SHA1 identity fingerprint. At most one may be listed.
 *       [03] Ed25519 identity
 *            A 32-byte Ed25519 identity fingerprint. At most one may
 *            be listed.
 *
 *        LSTYPE (Link specifier type)           [1 byte]
 *        LSLEN  (Link specifier length)         [1 byte]
 *        LSPEC  (Link specifier)                [LSLEN bytes]
 */

public class LinkSpecifier {

    public static final byte LSTYPE_IPV4 = 0x00;
    public static final byte LSTYPE_IPV6 = 0x01;
    public static final byte LSTYPE_SHA1 = 0x02;
    public static final byte LSTYPE_ED25519 = 0x04;

    private final byte LSTYPE;
    private final byte[] LSPEC;

    protected LinkSpecifier(byte LSTYPE, byte[] LSPEC) {
        this.LSTYPE = LSTYPE;
        this.LSPEC = LSPEC;
    }

    public byte[] getCellContent() {
        ByteBuffer b = ByteBuffer.allocate(1 + 1 + LSPEC.length);
        b.put(LSTYPE);
        b.put((byte) LSPEC.length);
        b.put(LSPEC);
        return b.array();
    }

    public byte getLSTYPE() {
        return LSTYPE;
    }

    public byte[] getLSPEC() {
        return LSPEC;
    }

    @Override
    public String toString() {
        return "LinkSpecifier{" +
                "LSTYPE=" + LSTYPE +
                '}';
    }
}
