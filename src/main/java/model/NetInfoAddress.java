package model;

import utils.ByteUtils;

public class NetInfoAddress {

    public static final byte TYPE_IPV4 = 0x04;
    public static final byte TYPE_IPV6 = 0x06;

    private final byte TYPE;
    private final byte[] ADDRESS_VALUE;

    public NetInfoAddress(byte TYPE, byte[] ADDRESS_VALUE) {
        this.TYPE = TYPE;
        this.ADDRESS_VALUE = ADDRESS_VALUE;
    }

    public byte getTYPE() {
        return TYPE;
    }

    public byte[] getADDRESS_VALUE() {
        return ADDRESS_VALUE;
    }

    public String getAddressAsString() {
        StringBuilder builder = new StringBuilder();

        switch (TYPE) {
            case TYPE_IPV4:
                for (byte subAddr : ADDRESS_VALUE) {
                    builder.append(ByteUtils.toUnsigned(subAddr) + ".");
                }
                builder.deleteCharAt(builder.length() - 1);
                break;
            case TYPE_IPV6:
                for (int i = 0; i < ADDRESS_VALUE.length; i += 2) {
                    builder.append(String.format("%02X%02X:", ADDRESS_VALUE[i], ADDRESS_VALUE[i + 1]));
                }
                builder.deleteCharAt(builder.length() - 1);
                break;
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "NetInfoAddress{" +
                "TYPE=" + TYPE +
                ", ADDRESS=" + getAddressAsString() +
                '}';
    }
}
