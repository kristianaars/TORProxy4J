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
        System.out.println("Connecting you to " + address + ":" + port + " trough the circuit...");
        final TorSocket socket = new TorSocket(address, port, circ);
        final BufferedReader reader;
        final TorOutputStream out;

        socket.waitForConnection();

        System.out.println("You are now connected to " + address + ":" + port + " trough a random TOR-Circuit.");
        System.out.println("This is the TOR-Circuit: ");
        System.out.println(circ.getRelayInfo());

        System.out.println("");
        System.out.println("Press enter to send a new line-message over TELNET. Type \"exit\" to close the telnet client.");

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = (TorOutputStream) socket.getOutputStream();

            final Scanner userInput = new Scanner(System.in);

            new Thread(() -> {
                while (true) {
                    try {
                        String response = reader.readLine();
                        if (response == null) {
                            System.out.println("Server requested to close...");
                            socket.close();
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
                out.write((input + "\r\n").getBytes());
            }

            System.out.println("Thank you for using TOR Telnet Client!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.closeCircuit();

        }


    }

}
