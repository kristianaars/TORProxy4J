package model.payload;

import exceptions.PayloadSizeNotFixedException;
import model.NetInfo;
import model.NetInfoAddress;
import utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class NetInfoPayload extends Payload {

    public NetInfoPayload(byte[] payload) throws PayloadSizeNotFixedException {
        super(payload);

        this.isFixedSize = true;
        if(payload.length != FIXED_PAYLOAD_SIZE) {
            throw new PayloadSizeNotFixedException(FIXED_PAYLOAD_SIZE);
        }
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
            index+= addressLength - 1;

            ownAddresses[i] = new NetInfoAddress(addressType, addressValue);
        }

        return new NetInfo(time, otherAddress, ownAddresses);
    }

    public static NetInfoPayload createPayloadFrom(NetInfo netInfo) {
        NetInfoAddress otherAddress = netInfo.getOTHER_ADDRESS();
        NetInfoAddress[] myAddresses = netInfo.getMY_ADDRESSES();

        //Calculate buffer size
        int bufferLength = FIXED_PAYLOAD_SIZE; //Fixed-size buffer

        //Begin payload-write to buffer
        ByteBuffer pumpBuffer = ByteBuffer.allocate(bufferLength);

        //Header (Timestamp)
        pumpBuffer.putInt(netInfo.getTIME());

        //Other address
        pumpBuffer.put(otherAddress.getTYPE());
        pumpBuffer.put((byte) (otherAddress.getADDRESS_VALUE().length & 0xFF));
        pumpBuffer.put(otherAddress.getADDRESS_VALUE());

        //My addresses
        pumpBuffer.put((byte) (myAddresses.length & 0xFF));
        for(NetInfoAddress a : myAddresses) {
            pumpBuffer.put(a.getTYPE());
            pumpBuffer.put((byte) (a.getADDRESS_VALUE().length & 0xFF));
            pumpBuffer.put(a.getADDRESS_VALUE());
        }

        try {
            return new NetInfoPayload(pumpBuffer.array());
        } catch (PayloadSizeNotFixedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
