package crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 *
 * Downloaded from: https://github.com/hoijui/JTor
 *
 * Copyright (c) 2009-2011, Bruce Leidl
 * All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class TorStreamCipher {
    public static final int KEY_LEN = 16;

    public static TorStreamCipher createWithRandomKey() throws InvalidKeyException {
        final SecretKey randomKey = generateRandomKey();
        return new TorStreamCipher(randomKey.getEncoded());
    }

    public static TorStreamCipher createFromKeyBytes(byte[] keyBytes) throws InvalidKeyException {
        return new TorStreamCipher(keyBytes);
    }
    private static final int BLOCK_SIZE = 16;
    private final Cipher cipher;
    private final byte[] counter;
    private final byte[] counterOut;
    /* Next byte of keystream in counterOut */
    private int keystreamPointer = -1;
    private final SecretKeySpec key;


    private TorStreamCipher(byte[] keyBytes) throws InvalidKeyException {
        key = keyBytesToSecretKey(keyBytes);
        cipher = createCipher(key);
        counter = new byte[BLOCK_SIZE];
        counterOut = new byte[BLOCK_SIZE];
    }

    public void encrypt(byte[] data) {
        encrypt(data, 0, data.length);
    }

    public synchronized void encrypt(byte[] data, int offset, int length) {
        for(int i = 0; i < length; i++)
            data[i + offset] ^= nextKeystreamByte();
    }

    public byte[] getKeyBytes() {
        return key.getEncoded();
    }

    private static SecretKeySpec keyBytesToSecretKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static Cipher createCipher(SecretKeySpec keySpec) throws InvalidKeyException {
        try {
            final Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SecretKey generateRandomKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES", "BC");
            generator.init(128);
            return generator.generateKey();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte nextKeystreamByte() {
        if(keystreamPointer == -1 || (keystreamPointer >= BLOCK_SIZE))
            updateCounter();
        return counterOut[keystreamPointer++];
    }
    private void updateCounter() {
        encryptCounter();
        incrementCounter();
        keystreamPointer = 0;
    }

    private void encryptCounter() {
        try {
            cipher.doFinal(counter, 0, BLOCK_SIZE, counterOut, 0);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private void incrementCounter() {
        int carry = 1;
        for(int i = counter.length - 1; i >= 0; i--) {
            int x = (counter[i] & 0xff) + carry;
            if(x > 0xff)
                carry = 1;
            else
                carry = 0;
            counter[i] = (byte)x;
        }
    }

}
