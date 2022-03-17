package model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;

public class TorRelay {

    private InetAddress address;
    private int port;
    private PublicKey onionKey;

    public TorRelay(String address, int port, PublicKey onionKey)  {
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            this.address = null;
        }
        this.port = port;
        this.onionKey = onionKey;
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getAddressAsString() {
        return address.getHostAddress();
    }

    public int getPort() {
        return port;
    }

    public PublicKey getOnionKey() {
        return onionKey;
    }
}
