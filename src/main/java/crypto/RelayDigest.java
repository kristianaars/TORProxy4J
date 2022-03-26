package crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.ReentrantLock;

public class RelayDigest {

    private final ReentrantLock digestLock;

    private final static String ALGORITHM = "SHA-1";
    private MessageDigest digest;

    public RelayDigest(byte[] seed) {
        digestLock = new ReentrantLock();
        try {
            digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(seed);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            digest = null;
        }
    }

    /**
     * Returns digest without resetting digest-object.
     *
     * @return Digest of all posted updates.
     */
    public byte[] getCurrentDigest() {
        try {
            digestLock.lock();
            MessageDigest cloneMd = (MessageDigest) digest.clone();
            digestLock.unlock();
            return cloneMd.digest();
        } catch (CloneNotSupportedException e) {
            digestLock.unlock();
            e.printStackTrace();
            return null;
        }
    }

    public byte[] update(byte[] input) {
        digestLock.lock();
        digest.update(input);
        digestLock.unlock();
        return getCurrentDigest();
    }
}
