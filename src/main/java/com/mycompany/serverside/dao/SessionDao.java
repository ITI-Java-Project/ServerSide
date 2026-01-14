package com.mycompany.serverside.dao;

import com.mycompany.serverside.dto.SessionDto;
import data.DbManager;
import java.sql.ResultSet;

public class SessionDao {

    // Use UPPERCASE column names (Derby default)
    private static final String GET_SESSION_BY_PLAYERS
            = "SELECT * FROM SESSION WHERE (PLAYER1ID = ? AND PLAYER2ID = ?) OR (PLAYER1ID = ? AND PLAYER2ID = ?)";

    private static final String GET_SESSION_BY_ID
            = "SELECT * FROM SESSION WHERE ID = ?";

    private static final String INSERT_SESSION
            = "INSERT INTO SESSION "
            + "(PLAYER1ID, PLAYER2ID, PLAYER1NAME, PLAYER2NAME, PLAYER1SCORE, PLAYER2SCORE) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SESSION_SCORES
            = "UPDATE SESSION SET PLAYER1SCORE = ?, PLAYER2SCORE = ? WHERE ID = ?";

    /**
     * Get session between two players regardless of order
     */
    public static SessionDto getSessionByPlayers(int player1Id, int player2Id) {
        try {
            ResultSet rs = DbManager.init()
                    .getQueryPrepared(GET_SESSION_BY_PLAYERS, player1Id, player2Id, player2Id, player1Id);

            if (rs != null && rs.next()) {
                return new SessionDto(
                        rs.getInt("ID"),
                        rs.getInt("PLAYER1ID"),
                        rs.getInt("PLAYER2ID"),
                        rs.getString("PLAYER1NAME"),
                        rs.getString("PLAYER2NAME"),
                        rs.getInt("PLAYER1SCORE"),
                        rs.getInt("PLAYER2SCORE")
                );
            }
        } catch (Exception ex) {
            System.err.println("Error getting session data: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static SessionDto getSessionById(int sessionId) {
        try {
            ResultSet rs = DbManager.init()
                    .getQueryPrepared(GET_SESSION_BY_ID, sessionId);

            if (rs != null && rs.next()) {
                return new SessionDto(
                        rs.getInt("ID"),
                        rs.getInt("PLAYER1ID"),
                        rs.getInt("PLAYER2ID"),
                        rs.getString("PLAYER1NAME"),
                        rs.getString("PLAYER2NAME"),
                        rs.getInt("PLAYER1SCORE"),
                        rs.getInt("PLAYER2SCORE")
                );
            }
        } catch (Exception ex) {
            System.err.println("Error getting session by ID: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public static SessionDto createSession(int player1Id, int player2Id, String player1Name, String player2Name) {
        System.out.println("Creating NEW session for players: " + player1Name + " vs " + player2Name);
        
        int sessionId = DbManager.init()
                .insertAndGetId(INSERT_SESSION, player1Id, player2Id, player1Name, player2Name, 0, 0);

        if (sessionId == -1) {
            System.err.println("Failed to create session in database");
            return null;
        }

        System.out.println("New session created with ID: " + sessionId);
        return new SessionDto(sessionId, player1Id, player2Id, player1Name, player2Name, 0, 0);
    }

    public static boolean updateSessionScores(int sessionId, int player1Score, int player2Score) {
        System.out.println("Updating session " + sessionId + " scores: P1=" + player1Score + ", P2=" + player2Score);
        
        boolean result = DbManager.init().updateQueryPrepared(
                UPDATE_SESSION_SCORES, 
                player1Score, 
                player2Score, 
                sessionId
        );
        
        if (result) {
            System.out.println("Session scores updated successfully");
        } else {
            System.err.println("Failed to update session scores");
        }
        
        return result;
    }
}