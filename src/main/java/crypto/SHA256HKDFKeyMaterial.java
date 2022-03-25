package crypto;

import utils.ByteUtils;

import java.util.Arrays;

public class SHA256HKDFKeyMaterial {

    /**
     *   When used in the ntor handshake, the first HASH_LEN bytes form the
     *    forward digest Df; the next HASH_LEN form the backward digest Db; the
     *    next KEY_LEN form Kf, the next KEY_LEN form Kb, and the final
     *    DIGEST_LEN bytes are taken as a nonce to use in the place of KH in the
     *    hidden service protocol.  Excess bytes from K are discarded.
     *
     * @param expandedKeyData Key-data derived from NTorHandshake and SHA256-HKDF
     */

    private final byte[] DF;
    private final byte[] DB;

    //Forward key
    private final byte[] KF;

    //Backwards key
    private final byte[] KB;

    public SHA256HKDFKeyMaterial(byte[] expandedKeyData) {
        this.DF = deriveDf(expandedKeyData);
        this.DB = deriveDb(expandedKeyData);
        this.KF = deriveKf(expandedKeyData);
        this.KB = deriveKb(expandedKeyData);
    }

    public byte[] getDF() {
        return DF;
    }

    public byte[] getDB() {
        return DB;
    }

    /**
     * Returns the Key for forward encryption
     * @return Key for forward encryption
     */
    public byte[] getKF() {
        return KF;
    }

    /**
     * Returns the Key for backwards decryption
     * @return Key for backwards decryption
     */
    public byte[] getKB() {
        return KB;
    }

    @Override
    public String toString() {
        return "SHA256HKDFKeyMaterial{" +
                "DF=" + ByteUtils.toHexString(DF) +
                ", DB=" + ByteUtils.toHexString(DB) +
                ", KF=" + ByteUtils.toHexString(KF) +
                ", KB=" + ByteUtils.toHexString(KB) +
                '}';
    }

    /**
     * ...the first HASH_LEN bytes form the forward digest Df;
     */
    private byte[] deriveDf(byte[] expandedKeyData) {
        return Arrays.copyOfRange(expandedKeyData,
                0,
                CryptoConstants.HASH_LEN
        );
    }

    /**
     * ...the next HASH_LEN form the backward digest Db;
     */
    private byte[] deriveDb(byte[] expandedKeyData) {
        return Arrays.copyOfRange(expandedKeyData,
                CryptoConstants.HASH_LEN,
                CryptoConstants.HASH_LEN + CryptoConstants.HASH_LEN
        );
    }

    /**
     * ...the next KEY_LEN form Kf,
     */
    private byte[] deriveKf(byte[] expandedKeyData) {
        return Arrays.copyOfRange(expandedKeyData,
                CryptoConstants.HASH_LEN*2,
                CryptoConstants.HASH_LEN*2 + CryptoConstants.KEY_LEN
        );
    }

    /**
     *  ...the next KEY_LEN form Kb,
     */
    private byte[] deriveKb(byte[] expandedKeyData) {
        return Arrays.copyOfRange(expandedKeyData,
                CryptoConstants.HASH_LEN*2 + CryptoConstants.KEY_LEN,
                CryptoConstants.HASH_LEN*2 + CryptoConstants.KEY_LEN + CryptoConstants.KEY_LEN
        );
    }



}
