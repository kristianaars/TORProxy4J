package model;

import java.nio.ByteBuffer;

public class LinkSpecifierGenerator {

    public static LinkSpecifier createIPv4LinkSpecifier(byte[] address, short port) {
        ByteBuffer pumpBuffer = ByteBuffer.allocate(address.length + 2);
        pumpBuffer.put(address);
        pumpBuffer.putShort(port);
        return new LinkSpecifier(LinkSpecifier.LSTYPE_IPV4, pumpBuffer.array());
    }

    public static LinkSpecifier createSHA1LinkSpecifier(byte[] sha1Fingerprint) {
        ByteBuffer pumpBuffer = ByteBuffer.allocate(sha1Fingerprint.length);
        pumpBuffer.put(sha1Fingerprint);
        return new LinkSpecifier(LinkSpecifier.LSTYPE_SHA1, pumpBuffer.array());
    }

    public static LinkSpecifier createED25519LinkSpecifier(byte[] ed25519Fingerprint) {
        ByteBuffer pumpBuffer = ByteBuffer.allocate(ed25519Fingerprint.length);
        pumpBuffer.put(ed25519Fingerprint);
        return new LinkSpecifier(LinkSpecifier.LSTYPE_ED25519, pumpBuffer.array());
    }

}
