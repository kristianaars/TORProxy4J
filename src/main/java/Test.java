import connection.Circuit;
import connection.CircuitBuilder;
import exceptions.UnexpectedDestroyException;
import connection.relay.TorRelay;
import connection.relay.RelayDirectory;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws UnexpectedDestroyException, IOException {
        CircuitBuilder builder = new CircuitBuilder(new TorRelay[]{
                RelayDirectory.getInstance().pickRandomRelay(),
                RelayDirectory.getInstance().pickRandomRelay(),
                RelayDirectory.getInstance().pickRandomRelay()
        });

        Circuit c = builder.buildCircuit();
    }

}
