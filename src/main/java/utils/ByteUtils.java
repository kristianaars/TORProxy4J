package utils;

import java.nio.charset.StandardCharsets;

public class ByteUtils {

    /**
     * Returns a number of type short represented by the provided bytes.
     *
     * @param b1 Front byte
     * @param b2 Latter byte
     * @return A short, represented in big endian order by the provided bytes.
     */
    public static short toShort(byte b1, byte b2) {
        return (short) ((short) (b1 << 8) | toUnsigned(b2));
    }

    public static int toUnsigned(short s) {
        return s & 0xFFFF;
    }

    public static int toUnsigned(byte s) {
        return s & 0xFF;
    }

    /**
     * Creates a hexadecimal string-representation of the provided byte-array
     *
     * @param byteBuffer Array to be represented
     * @return Hexadecimal string-representation of byte-buffer.
     */
    public static String toHexString(byte[] byteBuffer) {
        StringBuilder p = new StringBuilder();
        p.append("[");
        for(byte b : byteBuffer) { p.append(toHexString(b)); }
        p.append("]");
        return p.toString();
    }

    public static String toHexString(byte b) {
        return String.format("0x%02X ", b);
    }

    public static String toHexString(short s) { return String.format("0x%04X ", s); }

    public static String toHexString(int i) { return String.format("0x%02X ", i); }

    public static String toCharRepresentation(byte[] b) {
        StringBuilder p = new StringBuilder();
        for(byte by : b) {
            p.append((char) by);
        }

        return p.toString();
    }

    public static int toInt(byte[] bytes) {
        int r = 0;
        for(int i = 0; i < bytes.length && i < 4; i++) {
            r <<= 8;
            r |= (int)bytes[i] & 0xFF;
        }
        return r;
    }

    /**
     * Creates a byte-buffer based on the provided stream of hexadecimal-string. E.g of Hex-stream: FFBCE4 -> byte[]{255, 188, 228}
     *
     * Source: https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
     *
     * @param hexSequence Hex sequence to be converted to byte-array. (Must be even-length)
     * @return Byte-array with the bytes provided in the hex-sequence.
     */
    public static byte[] hexStringToByteArray(String hexSequence) {
        int len = hexSequence.length();

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexSequence.charAt(i), 16) << 4)
                    + Character.digit(hexSequence.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Returns byte-buffer from string, using default charset UTF-8
     *
     * @param s String to convert
     * @return New byte-buffer with the string values represented.
     */
    public static byte[] toBytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Returns the largest number found in the array.
     *
     * @param l Array to be searched
     * @return Largest number in the array
     */
    public static short largest(short[] l) {
        short max = l[0];
        for(int i = 1; i < l.length;i++) {
            if(l[i] > max) max = l[i];
        }

        return max;
    }
}
