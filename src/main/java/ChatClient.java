import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    public static void main(String[] args){
        String serverAddress = "localhost";
        int port = 8080;

        try {
            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to server");

            new Thread(new IncomingMessagesHandler(socket)).start();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            out.println(username + " has joined the room");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("/exit")) {
                    break;
                }
                out.println(username + ": " + line);
            }
                socket.close();
                System.exit(0);
        } catch (IOException e){
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private static class IncomingMessagesHandler implements Runnable {
        private Socket socket;

        public IncomingMessagesHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null){
                    System.out.println(serverMessage);
                }
            } catch (IOException e){
                System.out.println("Server connection lost.");
            }
        }

    }
}
