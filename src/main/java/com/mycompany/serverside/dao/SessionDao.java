package com.mycompany.serverside.dao;

import com.mycompany.serverside.dto.SessionDto;
import data.DbManager;
import java.sql.ResultSet;

public class SessionDao {

    private static final String GET_PLAYERS_DATA
            = "SELECT * FROM SESSION WHERE PLAYER1ID = ? AND PLAYER2ID = ?";

    private static final String INSERT_SESSION
            = "INSERT INTO SESSION "
            + "(PLAYER1ID, PLAYER2ID, PLAYER1NAME, PLAYER2NAME, PLAYER1SCORE, PLAYER2SCORE) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

    public static SessionDto getSessionData(int player1Id, int player2Id) {
        try {
            ResultSet rs = DbManager.init()
                    .getQueryPrepared(GET_PLAYERS_DATA, player1Id, player2Id);

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
            ex.printStackTrace();
        }
        return null;
    }

    public static SessionDto createSession(int player1Id, int player2Id, String player1Name, String player2Name) {
        int sessionId = DbManager.init()
                .insertAndGetId(INSERT_SESSION, player1Id, player2Id, player1Name, player2Name, 0, 0);

        return new SessionDto(sessionId, player1Id, player2Id, player1Name, player2Name, 0, 0);
    }
}
