package model.relay;

import exceptions.DescriptorFieldNotFoundException;

import java.io.IOException;
import java.net.InetAddress;

public class TorRelay {

    private InetAddress address;
    private final int torPort;
    private final int descriptorPort;

    private RelayDescriptor descriptor;

    public TorRelay(String address, int port, int descriptorPort)   {
        try {
            this.address = InetAddress.getByName(address);
            this.descriptor = RelayDescriptor.getRelayDescriptorFor(address, descriptorPort);
        } catch (IOException | DescriptorFieldNotFoundException e) {
            e.printStackTrace();
            this.address = null;
            this.descriptor = null;
        }

        this.torPort = port;
        this.descriptorPort = descriptorPort;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getAddressAsString() {
        return address.getHostAddress();
    }

    public int getPort() {
        return torPort;
    }

    public RelayDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public String toString() {
        return "TorRelay{" +
                "address=" + address +
                ", torPort=" + torPort +
                ", descriptorPort=" + descriptorPort +
                ", descriptor=" + descriptor +
                '}';
    }
}
