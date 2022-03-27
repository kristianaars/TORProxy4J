package connection;

import connection.relay.RelayDirectory;
import connection.relay.RelayType;
import connection.relay.TorRelay;
import exceptions.TorException;

import javax.management.InstanceNotFoundException;
import java.io.IOException;

/**
 * The CircuitService is available to provide one shared Circuit for all classes that may need to share this.
 *
 */
public class CircuitService {

    private static CircuitService instance;

    private Circuit circuit;

    public static CircuitService getInstance() {
        if(instance == null) instance = new CircuitService();
        return instance;
    }

    /**
     * Builds a new circuit for the current instance. If any old circuit is available, they will first be closed and removed.
     *
     * @param relayCount Numbers of relays to use in the new circuit
     * @return The new circuit, this circuit also available trough {@link CircuitService#getCircuit()}
     *
     * @throws TorException Any Tor-related exceptions during Circuit-building
     * @throws IOException Any IO-Exceptions that may appear during Circuit-building.
     */
    public Circuit generateNewCircuit(int relayCount) throws TorException, IOException {
        TorRelay[] relays = new TorRelay[relayCount];

        if(circuit != null && circuit.isRunning) {
            circuit.close();
        }

        for(int i = 0; i < relays.length; i++) {
            TorRelay addRelay;
            if(i == 0) {
                addRelay = RelayDirectory.getInstance().pickRandomRelay(RelayType.ENTRY_NODE);
            } else if(i == relays.length - 1) {
                addRelay = RelayDirectory.getInstance().pickRandomRelay(RelayType.EXIT_NODE);
            } else {
                addRelay = RelayDirectory.getInstance().pickRandomRelay(RelayType.MIDDLE_NODE);
            }

            relays[i] = addRelay;
        }

        this.circuit = new CircuitBuilder(relays).buildCircuit();
        return this.circuit;
    }


    /**
     * Returns the circuit on the current instance. If no circuit is created an exception will be thrown.
     *
     * @return The Circuit on the current instance.
     * @throws InstanceNotFoundException If no Circuit has been created on the circuit-service.
     */
    public Circuit getCircuit() throws InstanceNotFoundException {
        if(circuit == null) {
            throw new InstanceNotFoundException("A circuit was not found on the current CircuitService");
        }

        return circuit;
    }
}
