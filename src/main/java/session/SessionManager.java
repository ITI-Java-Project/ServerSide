package session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.serverside.dto.Player;
import com.mycompany.serverside.dto.*;
import java.util.*;
import java.util.stream.Collectors;
import network.*;

public class SessionManager {

    // players waiting for a match
    private Queue<ClientHandler> waiting = new LinkedList<>();

    // map each player to his session
    private Map<ClientHandler, Session> sessions = new HashMap<>();


    
    public ClientHandler getClientByPlayerId(int playerId){
        if(waiting.isEmpty()){
            return null;        
        }
        
        for(ClientHandler c : waiting){
            if(c.getPlayer().getId() == playerId) {
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
        // TODO : Manage Session action
    }

    /**
     * Handle move coming from client row & col are matrix-based
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

        List<Player> availablePlayers = waiting.stream()
                .filter(c -> c != requester)
                .filter(c -> !isInSession(c))
                .map(c -> {
                    Player p = c.getPlayer();
                    if (p == null) {
                        return null;
                    }

                    if (p.getId() <= 0) {
                        Player dbPlayer = PlayerDAO.register(p.getName(), p.getEmail(), p.getPassword());
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
}
