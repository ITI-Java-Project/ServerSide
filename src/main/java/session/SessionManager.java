package session;

import java.util.*;
import network.*;

public class SessionManager {

    // players waiting for a match
    private Queue<ClientHandler> waiting = new LinkedList<>();

    // map each player to his session
    private Map<ClientHandler, Session> sessions = new HashMap<>();

    /**
     * Add new player to waiting queue
     */
    public synchronized void addPlayer(ClientHandler c) {
        waiting.add(c);

        // start game when we have 2 players
        if (waiting.size() >= 2) {

            ClientHandler p1 = waiting.poll();
            ClientHandler p2 = waiting.poll();

            Session session = new Session(p1, p2);

            sessions.put(p1, session);
            sessions.put(p2, session);

            p1.send("START X");
            p2.send("START O");
        }
    }

    /**
     * Handle move coming from client
     * row & col are matrix-based
     */
    public synchronized void handleMove(ClientHandler c, int row, int col) {
        Session session = sessions.get(c);

        if (session != null) {
            session.playMove(c, row, col);
        }
    }

    /**
     * Remove player (disconnect / game finished)
     */
    public synchronized void removePlayer(ClientHandler c) {

        // remove from waiting queue if exists
        waiting.remove(c);

        // remove session mapping
        Session session = sessions.remove(c);

        if (session != null) {
            // remove the other player from session map
            sessions.values().removeIf(s -> s == session);
        }
    }
}
