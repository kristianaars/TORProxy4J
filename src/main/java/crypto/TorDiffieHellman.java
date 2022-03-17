package crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class TorDiffieHellman {

    /**
     *   For Diffie-Hellman, unless otherwise specified, we use a generator
     *    (g) of 2.  For the modulus (p), we use the 1024-bit safe prime from
     *    rfc2409 section 6.2 whose hex representation is:
     *
     *      "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E08"
     *      "8A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B"
     *      "302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9"
     *      "A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE6"
     *      "49286651ECE65381FFFFFFFFFFFFFFFF"
     *
     *      https://gitweb.torproject.org/torspec.git/tree/tor-spec.txt, chap 0.3
     */
    private final static BigInteger P1024 = new BigInteger(
            "00FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E08"
                + "8A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B"
                + "302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9"
                + "A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE6"
                + "49286651ECE65381FFFFFFFFFFFFFFFF", 16);
    private final static BigInteger G = new BigInteger("2");
    private final static int PRIVATE_KEY_SIZE = 320;

    private final KeyPair keyPair;
    private final KeyAgreement keyAgreement;

    public TorDiffieHellman() {
        Security.addProvider(new BouncyCastleProvider());

        keyPair = generateKeyPair();
        keyAgreement = generateKeyAgreement();
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("DH", "BC");
            generator.initialize(new DHParameterSpec(P1024, G, PRIVATE_KEY_SIZE));
            return generator.generateKeyPair();
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }

    private KeyAgreement generateKeyAgreement() {
        try {
            KeyAgreement ka = KeyAgreement.getInstance("DH", "BC");
            ka.init(keyPair.getPrivate());
            return ka;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Key getCommonSecret(BigInteger otherPublic) throws InvalidKeyException {
        try {
            KeyFactory kf = KeyFactory.getInstance("DH", "BC");
            DHPublicKeySpec pubSpec = new DHPublicKeySpec(otherPublic, P1024, G);
            PublicKey key = kf.generatePublic(pubSpec);
            return keyAgreement.doPhase(key, true);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

}
