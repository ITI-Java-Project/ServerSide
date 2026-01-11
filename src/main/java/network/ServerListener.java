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
        String command = msg.split(" ", 2)[0];
        System.out.println("Message : " + msg + "command : " + command);

        switch (command) {
            case "MOVE":
                moveAction(msg, client);
                break;

            case "GET_AVAILABLE_PLAYERS":
                getAvailablePlayersAction(msg, client);
                break;

            case "REGISTER":
                registerAction(msg, client);
                break;

            case "LOGIN":
                loginAction(msg, client);
                break;

            case "INVITE":
                handleInvitationRequest(msg, client);
                break;

            case "INVITE_ACCEPT":
                System.out.println("Message : " + msg + "command : " + command);
                handleAcceptInivitation(msg, client);
                break;

            case "INVITE_REJECT":
                handleRejectInivitation(msg, client);
                break;

            default:
                client.send("INVALID_LOGIN_FORMAT");
        }
    }

    private void moveAction(String msg, ClientHandler client) {
        String[] parts = msg.split(" "); // [[Move] , [0] , [0]]
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
    }

    private void getAvailablePlayersAction(String msg, ClientHandler client) {
        String playersData = sessionManager.getAvailablePlayersAsJsonString(client);
        System.out.println("SessionManager Log : " + playersData);
        if (playersData.isEmpty()) {
            client.send("AVAILABLE_PLAYERS:NONE");
        } else {
            client.send("AVAILABLE_PLAYERS:" + playersData);
        }
    }

    private void registerAction(String msg, ClientHandler client) {
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
    }

    private void loginAction(String msg, ClientHandler client) {
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
    }

    private void handleInvitationRequest(String msg, ClientHandler client) {
        String[] parts = msg.split(" ");
        int playerId = Integer.parseInt(parts[1]);
        ClientHandler otherClient = sessionManager.getClientByPlayerId(playerId);

        otherClient.send("INVITE_FROM " + client.getPlayer().getId() + " " + client.getPlayer().getName());
    }

    private void handleAcceptInivitation(String msg, ClientHandler client) {
        String[] parts = msg.split(" ");
        int playerId = Integer.parseInt(parts[1]);
        ClientHandler otherClient = sessionManager.getClientByPlayerId(playerId);
        sessionManager.createSession(client, otherClient);
        System.out.println("START " + client.getPlayer().getName());
        System.out.println("START " + otherClient.getPlayer().getName());
        otherClient.send("START");
        client.send("START");
    }

    private void handleRejectInivitation(String msg, ClientHandler client) {
        String[] parts = msg.split(" ");
        int playerId = Integer.parseInt(parts[1]);
        ClientHandler otherClient = sessionManager.getClientByPlayerId(playerId);

        otherClient.send("INVITE_FROM " + client.getPlayer().getId() + " " + client.getPlayer().getName());
    }
}
