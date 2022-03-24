package crypto;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * The encryption services is a wrapper for AES 128-Bit CTR encryption and decryption.
 */

public class EncryptionService {


    private final SecretKey encryptionKey;
    private final SecretKey decryptionKey;

    private static final String ALGORITM = "AES/CTR/NoPadding";

    /**
     * Service-class for encryption used in the Tor-protocol. Uses AES 128-Bit CTR encryption.
     *
     * @param encryptionKey Key to be used for encryption
     * @param decryptionKey Key to be used for decryption
     */
    public EncryptionService(byte[] encryptionKey, byte[] decryptionKey) {
        this.encryptionKey = new SecretKeySpec(encryptionKey, "AES");
        this.decryptionKey = new SecretKeySpec(decryptionKey, "AES");
    }

    private IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        for(int i = 0; i < iv.length; i++) { iv[i] = 0; }
        return new IvParameterSpec(iv);
    }

    /**
     * Encrypt message with AES-128 Bit CTR encryption. Message must be pre-padded.
     * @param message Message to be encrypted
     * @return Encrypted message
     */
    public byte[] encrypt(byte[] message) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITM);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, generateIV());

            byte[] e = cipher.doFinal(message);
            return e;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] encryptedMessage) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITM);
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey);

            return cipher.doFinal(encryptedMessage);

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }
}
