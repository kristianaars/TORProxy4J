import connection.Circuit;
import connection.CircuitBuilder;
import crypto.EncryptionService;
import exceptions.CouldNotVerifyHandshakeException;
import exceptions.UnexpectedDestroyException;
import connection.relay.TorRelay;
import connection.relay.RelayDirectory;
import utils.ByteUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Test {

    public static void main(String[] args) throws UnexpectedDestroyException, IOException, NoSuchAlgorithmException, CouldNotVerifyHandshakeException {
        CircuitBuilder builder = new CircuitBuilder(new TorRelay[]{
                RelayDirectory.getInstance().pickRandomRelay(),
                RelayDirectory.getInstance().pickRandomRelay(),
                RelayDirectory.getInstance().pickRandomRelay()
        });

        Circuit c = builder.buildCircuit();

        //MessageDigest md = MessageDigest.getInstance("SHA-1");
        //md.update((byte) 0x02);
        //md.update((byte) 0x04);
        //System.out.println(ByteUtils.toHexString(md.digest()));
    }

}
