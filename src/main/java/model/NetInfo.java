package model;

import java.util.Arrays;

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
}

