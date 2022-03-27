import connection.*;
import exceptions.TorException;

import java.io.*;
import java.util.Scanner;

public class TelnetExample {

    public static void main(String[] args) throws IOException, TorException {
        String address = args[0];
        int port = Integer.parseInt(args[1]);

        System.out.println("Welcome to TOR-Telnet Client!");
        System.out.println("Creating TOR-Circuit, please wait...");
        Circuit circ = CircuitService.getInstance().generateNewCircuit(4);

        //TorSocket also uses the CircuitService-instance to create Tor-Stream.
        System.out.println("Connecting you to " + address + ":" + port + " trough the circuit");
        final TorSocket socket = new TorSocket(address, port, circ);
        final BufferedReader reader;
        final TorOutputStream out;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = (TorOutputStream) socket.getOutputStream();

            System.out.println("You are now connected to TOR trough " + circ.getRelayCount() + " random TOR nodes");
            System.out.println("These are the nodes: ");
            System.out.println(circ.getRelayInfo());

            final Scanner userInput = new Scanner(System.in);

            new Thread(() -> {
                while (true) {
                    try {
                        String response = reader.readLine();
                        if (response == null) {
                            socket.close();
                            System.out.println("Complete");
                            break;
                        };

                        System.out.println(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            String input = "";
            while (!(input = userInput.nextLine()).equals("exit") && !socket.isClosed()) {
                System.out.println(input.toCharArray());
                out.write((input + "\r\n").getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.closeCircuit();
        }
    }

}
