package network;

import com.mycompany.serverside.dto.PlayerDto;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import network.MessageListener;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageListener listener;
    private PlayerDto player;

    public ClientHandler(Socket socket, MessageListener listener) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                try {
                    listener.onMessage(msg, this);
                } catch (Exception e) {
                    System.out.println("Error handling message: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
            listener.removeClient(this);
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
            }
        }
    }

    public void send(String msg) {
        out.println(msg);
    }

    public PlayerDto getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDto player) {
        this.player = player;
    }
}
