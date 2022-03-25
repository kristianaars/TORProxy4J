package crypto;

import utils.ByteUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

/**
 * The encryption services is a wrapper for AES 128-Bit CTR encryption and decryption.
 */

public class EncryptionService {

    private final SecretKey encryptionKey;
    private final SecretKey decryptionKey;

    private static final String ALGORITM = "AES/CTR/NoPadding";

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    /**
     * Service-class for encryption used in the Tor-protocol. Uses AES 128-Bit CTR encryption.
     *
     * @param encryptionKey Key to be used for encryption
     * @param decryptionKey Key to be used for decryption
     */
    public EncryptionService(byte[] encryptionKey, byte[] decryptionKey) {
        this.encryptionKey = new SecretKeySpec(encryptionKey, 0, 16, "AES");
        this.decryptionKey = new SecretKeySpec(decryptionKey, 0, 16, "AES");

        this.encryptCipher = initEncryptCipher();
        this.decryptCipher = initDecryptCipher();
    }

    private IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        //All bytes equal zero
        return new IvParameterSpec(iv);
    }

    private Cipher initEncryptCipher() {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITM);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, generateIV());
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Cipher initDecryptCipher() {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITM);
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey, generateIV());
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encrypt message with AES-128 Bit CTR encryption. Message must be pre-padded.
     * @param message Message to be encrypted
     * @return Encrypted message
     */
    public byte[] encrypt(byte[] message) {
        byte[] res = encryptCipher.update(message);
        //System.out.println("Result: " + ByteUtils.toHexString(res));
        return res;
    }

    public byte[] decrypt(byte[] encryptedMessage) {
        byte[] res = decryptCipher.update(encryptedMessage);
        return res;
    }
}
