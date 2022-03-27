package connection.relay;

import java.io.IOException;
import java.net.InetAddress;

public class TorRelay {

    private InetAddress address;
    private final int torPort;
    private final int dirPort;

    private final boolean SUITABLE_ENTRY_NODE;
    private final boolean SUPPORTS_EXIT;
    private final boolean IS_DIRECTORY_RELAY;
    private boolean IS_ONLINE;

    private final String providedFingerprint;

    private RelayDescriptor descriptor;

    public TorRelay(String address, int port, int dirPort, String fingerprint, boolean suitableEntryNode, boolean supportsExit) {
        try {
            this.address = InetAddress.getByName(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.providedFingerprint = fingerprint;
        this.torPort = port;
        this.dirPort = dirPort;
        this.SUITABLE_ENTRY_NODE = suitableEntryNode;
        this.SUPPORTS_EXIT = supportsExit;

        if(dirPort <= 0) {
            IS_DIRECTORY_RELAY = false;
        } else { IS_DIRECTORY_RELAY = true; }
    }

    public boolean isSuitableEntryNode() {
        return SUITABLE_ENTRY_NODE;
    }

    public boolean supportsExit() {
        return SUPPORTS_EXIT;
    }

    public boolean isDirectoryNode() {
        return IS_DIRECTORY_RELAY;
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
                "Address=" + address +
                ", TorPort=" + torPort +
                ", Fingerprint=" + providedFingerprint +
                '}';
    }
}
