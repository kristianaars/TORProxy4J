package utils;

import org.bouncycastle.util.encoders.Base64;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


public class CryptoUtils {

    /**
     * reads a public key from a file
     * @param cert Certificate-string to be parsed
     * @param algorithm is usually RSA
     * @return the read public key
     * @throws Exception
     */
    public static PublicKey getPublicCertificateFrom(String cert, String algorithm) {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        String publicKeyPEM = cert.replace("\n", "");
        publicKeyPEM = cert.replace("-----BEGIN RSA PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END RSA PUBLIC KEY-----", "");

        byte[] decoded = Base64.decode(publicKeyPEM.getBytes(StandardCharsets.UTF_8));

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance(algorithm);
            return kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts an byte-array to a base64-encoded byte-buffer.
     *
     * @param b Byte-array to be converted
     * @return Base64 encoded byte-buffer
     */
    public static byte[] toBase64(byte[] b) {
        return Base64.encode(b);
    }

    public static byte[] decodeBase64(String encodedString) {
        return Base64.decode(encodedString);
    }

    public static byte[] decodeBase64(byte[] encodedBuffer) {
        return Base64.decode(encodedBuffer);
    }
}
