package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionManager {

    private ServerSocket serverSocket;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private MessageListener listener;

    public ConnectionManager(int port, MessageListener listener) throws Exception {
        this.serverSocket = new ServerSocket(port);
        this.listener = listener;
    }

    public void start() {
        System.out.println("Server started on port " + serverSocket.getLocalPort());

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                ClientHandler client = new ClientHandler(socket, listener);
                listener.addClient(client);

                pool.execute(client);
            } catch (IOException e) {
                // prevent infinite loop of : Connection Error : ----
                if (serverSocket.isClosed()) {
                    System.out.println("Server stopped.");
                    break;
                } else {
                    System.out.println("Connection error: " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Connection error: " + e.getMessage());
            }
        }
    }

    public void stopServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        // all clientHandler Threads Stopped
        pool.shutdownNow();
    }
}
