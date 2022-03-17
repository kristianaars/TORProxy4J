package factory;

import model.Certificate;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class CertificateFactory {

    private static CertificateFactory defaultInstance;

    public static CertificateFactory getInstance() {
        if(defaultInstance == null) defaultInstance = new CertificateFactory();
        return defaultInstance;
    }

    private CertificateFactory() {}

    public Certificate generateCertificate(byte type) {
        switch (type) {
            case 0x03:
                KeyPairGenerator kpg = null;
                try {
                    kpg = KeyPairGenerator.getInstance("RSA");
                    kpg.initialize(1024);
                    KeyPair keyPair = kpg.generateKeyPair();
                    Key pub = keyPair.getPublic();
                    return new Certificate(type, pub.getEncoded());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            default:
                return null;
        }
    }
}
