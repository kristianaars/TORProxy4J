package model.cell;

import utils.ByteUtils;

import java.util.Arrays;

public class NetInfoPayload extends Payload {

    public NetInfoPayload(byte[] payload) {
        super(payload);

    }

    public NetInfo readNetInfoData() {
        int index = 0;
        int time = ByteUtils.toInt(Arrays.copyOfRange(payload, index, index + 4));
        index += 3;

        byte otherAddressType = payload[++index];
        int otherAddressLength = ByteUtils.toUnsigned(payload[++index]);
        byte[] otherAddressValue = Arrays.copyOfRange(payload, ++index, index + otherAddressLength);
        index += otherAddressLength - 1;
        NetInfoAddress otherAddress = new NetInfoAddress(otherAddressType, otherAddressValue);

        int numberOfOwnAddresses = ByteUtils.toUnsigned(payload[++index]);
        NetInfoAddress[] ownAddresses = new NetInfoAddress[numberOfOwnAddresses];

        for(int i = 0; i < numberOfOwnAddresses; i++) {
            byte addressType = payload[++index];
            int addressLength =  ByteUtils.toUnsigned(payload[++index]);
            byte[] addressValue = Arrays.copyOfRange(payload, ++index, index +addressLength);
            index+= addressLength;

            ownAddresses[i] = new NetInfoAddress(addressType, addressValue);
        }

        return new NetInfo(time, otherAddress, ownAddresses);
    }
}
