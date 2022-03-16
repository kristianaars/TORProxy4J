package utils;

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
    public static String toString(byte[] byteBuffer) {
        StringBuilder p = new StringBuilder();
        p.append("[");
        for(byte b : byteBuffer) { p.append(toString(b)); }
        p.append("]");
        return p.toString();
    }

    public static String toString(byte b) {
        return String.format("0x%02X ", b);
    }
}
