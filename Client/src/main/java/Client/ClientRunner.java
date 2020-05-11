package Client;

import java.util.Scanner;

public class ClientRunner {
    public static final int PORT = 10274;

    public static void main(String[] args) {
        Client client = new Client(PORT);
        boolean connected = client.connect();

        if (!connected) {
            System.out.println("Can not connect to Server");
            System.exit(0);
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String[] queries = getCommand(scanner).split(";");
            for (String q : queries) {
                System.out.println(client.communicate(q + ";"));
            }
        }
    }

    public boolean run(int port) {
        Client client = new Client(port);
        boolean connected = client.connect();

        if (!connected) return false;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = getCommand(scanner);

            String[] queries = command.split(";");
            for (String q : queries) {
                if (q.equals("exit")) {
                    return true;
                }

                System.out.println(client.communicate(q + ";"));
            }
        }
    }

    private static String getCommand(Scanner scanner) {
        StringBuilder builder = new StringBuilder();

        System.out.print("# ");
        String line = scanner.nextLine().trim();
        builder.append(line);

        while (!line.endsWith(";")) {
            System.out.print("\t> ");
            line = scanner.nextLine().trim();
            builder.append(" ").append(line);
        }

        return builder.toString();
    }
}
