package model;

import java.util.Arrays;
import java.util.Objects;

public class NetInfo {

    private final int TIME;
    private final NetInfoAddress OTHER_ADDRESS;
    private final NetInfoAddress[] MY_ADDRESSES;

    public NetInfo(int timestamp, NetInfoAddress otherAddress, NetInfoAddress[] myAddresses) {
        this.TIME = timestamp;
        this.OTHER_ADDRESS = otherAddress;
        this.MY_ADDRESSES = myAddresses;
    }

    public int getTIME() {
        return TIME;
    }

    public NetInfoAddress getOTHER_ADDRESS() {
        return OTHER_ADDRESS;
    }

    public NetInfoAddress[] getMY_ADDRESSES() {
        return MY_ADDRESSES;
    }

    @Override
    public String toString() {
        return "NetInfo{" +
                "TIME=" + TIME +
                ", OTHER_ADDRESS=" + OTHER_ADDRESS +
                ", MY_ADDRESSES=" + Arrays.toString(MY_ADDRESSES) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetInfo netInfo = (NetInfo) o;

        if (TIME != netInfo.TIME) return false;
        if (!Objects.equals(OTHER_ADDRESS, netInfo.OTHER_ADDRESS))
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(MY_ADDRESSES, netInfo.MY_ADDRESSES);
    }

    @Override
    public int hashCode() {
        int result = TIME;
        result = 31 * result + (OTHER_ADDRESS != null ? OTHER_ADDRESS.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(MY_ADDRESSES);
        return result;
    }
}

