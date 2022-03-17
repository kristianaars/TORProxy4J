package utils;


import org.bouncycastle.util.encoders.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


public class CertificateUtils {

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

        String publicKeyPEM = cert.replace("-----BEGIN RSA PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END RSA PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("\n", "");

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
}
