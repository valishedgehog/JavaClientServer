package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private final int BUFFER_SIZE = 2048;
    private String hostname = "localhost";
    private final int port;

    private Socket socket;

    private PrintStream printStream = System.out;
    private PrintStream errorStream = System.err;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private int connectionAttempts = 0;

    public Client(String hostname, int port, PrintStream printStream, PrintStream errorStream) {
        this.hostname = hostname;
        this.port = port;

        this.printStream = printStream;
        this.errorStream = errorStream;
    }

    public Client(int port, PrintStream printStream, PrintStream errorStream) {
        this.port = port;

        this.printStream = printStream;
        this.errorStream = errorStream;
    }

    public Client(int port) {
        this.port = port;
    }

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String communicate(String query) {
        StringBuilder result = new StringBuilder();

        String[] queries = query.split("\n");
        for (String q : queries) {
            try {
                outputStream.write(q.trim().getBytes());
                outputStream.flush();

                if (q.equals("exit;")) {
                    disconnect();
                    System.exit(0);
                }

                while (inputStream.available() <= 0) ;

                byte[] buffer = new byte[BUFFER_SIZE];

                while (inputStream.available() > 0) {
                    inputStream.read(buffer);
                    result.append(new String(buffer).trim());
                    buffer = new byte[BUFFER_SIZE];
                }
            } catch (IOException e) {
                errorStream.println(e.getMessage());
                result = new StringBuilder("Connection lost");
                disconnect();
            }
        }

        return result.toString().trim();
    }

    public void disconnect() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            errorStream.println("Exception: " + e.getMessage());
        }

        printStream.println("Connection closed");
    }

    public boolean connect() {
        if (connectionAttempts == 5) {
            return false;
        }

        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            printStream.println("Connected to DBMS Server on port: " + port);
            connectionAttempts = 0;
            return true;
        } catch (Exception e) {
            connectionAttempts++;
            errorStream.println("Connection failed. Trying again");

            try {
                Thread.sleep(2000);
            } catch (InterruptedException interruptedException) {
                errorStream.println("Critical error: " + interruptedException.getMessage());
            }

            return connect();
        }
    }
}
