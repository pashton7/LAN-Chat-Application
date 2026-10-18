import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    private static final int port = 8080;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args){
        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter printWriter;
        private BufferedReader bufferedReader;

        public ClientHandler(Socket serverSocket){
            this.socket = serverSocket;
        }

        public void run(){
            BufferedReader in = null;
            PrintWriter out = null;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Track this client's writer to broadcast messages to them
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    broadcast(message);
                }
            } catch (IOException e) {
                System.out.println("A client disconnected.");
            } finally {
                if (out != null) {
                    synchronized (clientWriters) {
                        clientWriters.remove(out);
                    }
                }
                try { socket.close(); } catch (IOException e) {}
                if (clientWriters.isEmpty()) System.exit(0); // Automatically kill the server when clients leave
            }
        }

        private static void broadcast(String message){
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }

}
