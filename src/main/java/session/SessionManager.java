package session;

import com.mycompany.serverside.dao.PlayerDao;
import com.google.gson.Gson;
import com.mycompany.serverside.dao.SessionDao;
import com.mycompany.serverside.dto.PlayerDto;
import java.util.*;
import java.util.stream.Collectors;
import network.*;
import com.mycompany.serverside.dto.SessionDto;

public class SessionManager {

    // players waiting for a match
    private Queue<ClientHandler> waiting = new LinkedList<>();

    // map each player to his session
    private Map<ClientHandler, Session> sessions = new HashMap<>();

    private static int playersInSessionCount = 0;

    public ClientHandler getClientByPlayerId(int playerId) {
        if (waiting.isEmpty()) {
            return null;
        }

        for (ClientHandler c : waiting) {
            if (c.getPlayer().getId() == playerId) {
                return c;
            }
        }

        return null;
    }

    /**
     * Add new player to waiting queue
     */
    public synchronized void addPlayer(ClientHandler c) {
        System.out.println("Session Manaager add Player LOG : " + c.getPlayer());

        waiting.add(c);
        System.out.println("Session Manaager add Player LOG Length: " + waiting.size());
        // TODO : Manage SessionDto action
    }

    public synchronized SessionDto createSession(ClientHandler p1, ClientHandler p2) {

        waiting.remove(p1);
        waiting.remove(p2);

        Session session = new Session(p1, p2, this);
        playersInSessionCount += 2;

        sessions.put(p1, session);
        sessions.put(p2, session);

        p1.send("GAME_START X");
        p2.send("GAME_START O");

        //get session data of this two players
        int player1Id = p1.getPlayer().getId();
        int player2Id = p2.getPlayer().getId();
        String player1Name = p1.getPlayer().getName();
        String player2Name = p2.getPlayer().getName();

        SessionDto sessionObject = SessionDao.getSessionData(player1Id, player2Id);
        if (sessionObject == null) {
            sessionObject = SessionDao.createSession(player1Id, player2Id, player1Name, player2Name);
        }

        return sessionObject;
    }

    public synchronized void finishSession(Session session) {

        ClientHandler p1 = null;
        ClientHandler p2 = null;

        for (Map.Entry<ClientHandler, Session> entry : sessions.entrySet()) {
            if (entry.getValue() == session) {
                if (p1 == null) {
                    p1 = entry.getKey();
                } else {
                    p2 = entry.getKey();
                }
            }
        }

        // remove session
        sessions.entrySet().removeIf(e -> e.getValue() == session);

        if (playersInSessionCount >= 2) {
            playersInSessionCount -= 2;
        }

        // add players back to waiting list
        if (p1 != null) {
            waiting.add(p1);
        }
        if (p2 != null) {
            waiting.add(p2);
        }

        System.out.println("Session finished, players returned to waiting list");
    }

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
        System.out.println("Remove Player Log : " + c.getPlayer().getName());

        // remove session mapping
        Session session = sessions.remove(c);

        if (session != null) {
            // remove the other player from session map
            sessions.values().removeIf(s -> s == session);
        }
    }

    public synchronized String getAvailablePlayersAsJsonString(ClientHandler requester) {
        System.out.println("Session Manager Log : " + waiting.size());

        List<PlayerDto> availablePlayers = waiting.stream()
                .filter(c -> c != requester)
                .filter(c -> !isInSession(c))
                .map(c -> {
                    PlayerDto p = c.getPlayer();
                    if (p == null) {
                        return null;
                    }

                    if (p.getId() <= 0) {
                        PlayerDto dbPlayer = PlayerDao.register(p.getName(), p.getEmail(), p.getPassword());
                        if (dbPlayer != null) {
                            c.setPlayer(dbPlayer);
                            p = dbPlayer;
                        } else {
                            return null;
                        }
                    }
                    return p;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (availablePlayers.isEmpty()) {
            return "NONE";
        }

        return new Gson().toJson(availablePlayers);
    }

    public boolean isInSession(ClientHandler client) {
        return sessions.containsKey(client);
    }

    public Queue<ClientHandler> getWaitingClients() {
        return waiting;
    }

    public static int getPlayersInSessionCount() {
        return playersInSessionCount;
    }
}
