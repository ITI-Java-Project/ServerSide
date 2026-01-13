package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
            String name = parts[1];
            String email = parts[2];
            String password = parts[3];

            Player player = PlayerDAO.register(name, email, password);

            if (player != null) {
                client.setPlayer(player);
                sessionManager.addPlayer(client);

                Gson gson = new GsonBuilder().create();
                String playerJson = gson.toJson(player);

                client.send("REGISTER_SUCCESS " + playerJson);
            } else {
                client.send("REGISTER_FAILED email_already_exists");
            }
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

                Gson gson = new GsonBuilder().create();
                String playerJson = gson.toJson(player);

                client.send("LOGIN_SUCCESS " + playerJson);
            } else {
                client.send("LOGIN_FAILED username_or_password_invalid");
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

        otherClient.send("INVITE_REJECT " + client.getPlayer().getId() + " " + client.getPlayer().getName());
    }
}
