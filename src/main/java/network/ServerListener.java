package network;

import session.Session;
import session.SessionManager;

public class ServerListener implements MessageListener {

    private SessionManager sessionManager;

    public ServerListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void addClient(ClientHandler client) {
        sessionManager.addPlayer(client);
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
        }
    }
}
