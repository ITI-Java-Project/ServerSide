package network;

import com.mycompany.serverside.dto.*;
import session.SessionManager;

public class ServerListener implements MessageListener {

    private SessionManager sessionManager;

    public ServerListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void addClient(ClientHandler client) {
//        sessionManager.addPlayer(client);
    }

    public void removeClient(ClientHandler client) {
        sessionManager.removePlayer(client);
    }

    @Override
    public void onMessage(String msg, ClientHandler client) {
        System.out.println("Received from client: " + msg);

        // MOVE row col
        if (msg.startsWith("MOVE")) {
            String[] parts = msg.split(" ");
            if (parts.length >= 3) {
                try {
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);

                    sessionManager.handleMove(client, row, col);

                } catch (NumberFormatException e) {
                    client.send("INVALID_MOVE_FORMAT");
                }
            } else {
                client.send("INVALID_MOVE_FORMAT");
            }
        } else if (msg.equals("GET_AVAILABLE_PLAYERS")) {
            String playersData = sessionManager.getAvailablePlayersAsJsonString(client);
            System.out.println("SessionManager Log : " + playersData);
            if (playersData.isEmpty()) {
                client.send("AVAILABLE_PLAYERS:NONE");
            } else {
                client.send("AVAILABLE_PLAYERS:" + playersData);
            }
        } else if (msg.startsWith("REGISTER")) {
            String[] parts = msg.split(" ");
            if (parts.length == 4) {
                String username = parts[1];
                String email = parts[2];
                String password = parts[3];

                Player player = PlayerDAO.register(username, email, password);
                if (player != null) {
                    client.setPlayer(player);
                    sessionManager.addPlayer(client);
                    client.send("REGISTERED");
                } else {
                    client.send("REGISTER_FAILED");
                }
            } else {
                client.send("INVALID_REGISTER_FORMAT");
            }
        } else if (msg.startsWith("LOGIN")) {
            String[] parts = msg.split(" ");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];

                Player player = PlayerDAO.login(username, password);
                if (player != null) {
                    client.setPlayer(player);
                    sessionManager.addPlayer(client);
                    client.send("LOGIN_SUCCESS");
                } else {
                    client.send("LOGIN_FAILED : username or password isn't correct");
                }
            }

        } else {
            client.send("INVALID_LOGIN_FORMAT");
        }
    }
}
