package utils;


import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObjectParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
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

    public static PublicKey readPublicKey(String cert) {
        try {
            PEMParser pemParser = new PEMParser(new StringReader(cert));
            Object object = pemParser.readObject();
            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) object;

            RSAKeyParameters rsa = (RSAKeyParameters) PublicKeyFactory.createKey(subjectPublicKeyInfo);

            RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(rsa.getModulus(), rsa.getExponent());
            KeyFactory kf = KeyFactory.getInstance("RSA", new BouncyCastleProvider());
            return kf.generatePublic(rsaSpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
