package connection.relay;

import java.io.IOException;
import java.net.InetAddress;

public class TorRelay {

    private InetAddress address;
    private final int torPort;
    private final int dirPort;

    private final String providedFingerprint;

    private RelayDescriptor descriptor;

    public TorRelay(String address, int port, String fingerprint)   {
        try {
            this.address = InetAddress.getByName(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.providedFingerprint = fingerprint;
        this.torPort = port;
        this.dirPort = 80;
    }

    public TorRelay(String address, int port, int dirPort, String fingerprint)   {
        try {
            this.address = InetAddress.getByName(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.providedFingerprint = fingerprint;
        this.torPort = port;
        this.dirPort = dirPort;
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
        if(descriptor == null) {
            try {
                this.descriptor = RelayDescriptor.getRelayDescriptorFor(address.getHostAddress(), providedFingerprint);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return descriptor;
    }

    public String getFingerprint() {
        return providedFingerprint;
    }

    public int getDirPort() {
        return dirPort;
    }

    @Override
    public String toString() {
        return "TorRelay{" +
                "address=" + address +
                ", torPort=" + torPort +
                ", descriptor=" + descriptor +
                '}';
    }
}
