package crypto;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;

public class SHA256HKDF {

    private final HKDFBytesGenerator generator;

    public SHA256HKDF(byte[] ikm, byte[] salt, byte[] info) {
        generator = new HKDFBytesGenerator(new SHA256Digest());
        generator.init(new HKDFParameters(ikm, salt, info));
    }

    public SHA256HKDFKeyMaterial hkdfExpand() {
        int length = CryptoConstants.HKDF_DIGEST_LENGTH;

        byte[] buffer = new byte[length];
        generator.generateBytes(buffer, 0, length);
        return new SHA256HKDFKeyMaterial(buffer);
    }
}
