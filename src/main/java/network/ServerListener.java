package network;

import com.mycompany.serverside.dao.PlayerDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.serverside.dto.*;
import java.util.List;
import java.util.Queue;
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
            case "GET_ALL_PLAYERS":
                getAllPlayersAction(client);
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

    private void getAllPlayersAction(ClientHandler client) {

        List<PlayerDto> players = PlayerDao.getAllPlayers();

        if (players == null || players.isEmpty()) {
            client.send("ALL_PLAYERS:NONE");
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(players);

        client.send("ALL_PLAYERS:" + json);
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

            PlayerDto player = PlayerDao.register(name, email, password);

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

            PlayerDto player = PlayerDao.login(username, password);
            if (player != null) {
                if (!checkClientStatusLoggedOrNot(player)) {
                    client.setPlayer(player);
                    sessionManager.addPlayer(client);

                    Gson gson = new GsonBuilder().create();
                    String playerJson = gson.toJson(player);

                    client.send("LOGIN_SUCCESS " + playerJson);
                } else {
                    client.send("LOGIN_FAILED you already logged in");
                }

            } else {
                client.send("LOGIN_FAILED username_or_password_invalid");
            }
        }
    }

    private boolean checkClientStatusLoggedOrNot(PlayerDto player) {
        Queue<ClientHandler> loggedClients = sessionManager.getWaitingClients();
        for (ClientHandler client : loggedClients) {
            if (client.getPlayer().getId() == player.getId()) {
                return true;
            }
        }
        return false;
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

        SessionDto sessionData = sessionManager.createSession(client, otherClient);

        System.out.println("START " + client.getPlayer().getName());
        System.out.println("START " + otherClient.getPlayer().getName());
        otherClient.send("START");
        client.send("START");

        //send ession data to playes as json
        sendSessionDataAsJson(sessionData, client, otherClient);
    }

    private void handleRejectInivitation(String msg, ClientHandler client) {
        String[] parts = msg.split(" ");
        int playerId = Integer.parseInt(parts[1]);
        ClientHandler otherClient = sessionManager.getClientByPlayerId(playerId);

        otherClient.send("INVITE_REJECT " + client.getPlayer().getId() + " " + client.getPlayer().getName());
    }

    private void sendSessionDataAsJson(SessionDto sessionData, ClientHandler c1, ClientHandler c2) {
        Gson gson = new GsonBuilder().create();
        String sessionJson = gson.toJson(sessionData);

        c1.send("SessionData " + sessionJson);
        c2.send("SessionData " + sessionJson);
    }
}
