package session;

import java.util.*;
import network.*;

public class SessionManager {

    private Queue<ClientHandler> waiting = new LinkedList<>();
    private Map<ClientHandler, Session> sessions = new HashMap<>();

    public synchronized void addPlayer(ClientHandler c) {
        waiting.add(c);

        if (waiting.size() >= 2) {
            ClientHandler p1 = waiting.poll();
            ClientHandler p2 = waiting.poll();

            Session s = new Session(p1, p2);
            sessions.put(p1, s);
            sessions.put(p2, s);

            p1.send("START X");
            p2.send("START O");
        }
    }

    public synchronized void handleMove(ClientHandler c, int idx) {
        Session s = sessions.get(c);
        if (s != null) s.playMove(c, idx);
    }
}
