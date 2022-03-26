import connection.Circuit;
import connection.CircuitBuilder;
import connection.TorStream;
import connection.relay.RelayType;
import crypto.EncryptionService;
import exceptions.CouldNotVerifyHandshakeException;
import exceptions.NTorHandshakeException;
import exceptions.TorException;
import exceptions.UnexpectedDestroyException;
import connection.relay.TorRelay;
import connection.relay.RelayDirectory;
import utils.ByteUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws TorException, IOException, NoSuchAlgorithmException {
        CircuitBuilder builder = new CircuitBuilder(new TorRelay[]{
                RelayDirectory.getInstance().pickRandomRelay(RelayType.ENTRY_NODE),
                RelayDirectory.getInstance().pickRandomRelay(RelayType.MIDDLE_NODE),
                RelayDirectory.getInstance().pickRandomRelay(RelayType.EXIT_NODE)
        });

        Circuit c = builder.buildCircuit();
        TorStream s = c.createStream(new InetSocketAddress("129.241.162.40", 80));

        //System.out.println(req.length());

        Scanner sc = new Scanner(System.in);

        while (true) {
            sc.nextLine();
            s.sendData("GET / HTTP/1.1\r\nHost:datakom.no\r\n\r\n".getBytes());
        }
    }

}
