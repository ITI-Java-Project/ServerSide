package com.mycompany.serverside.dao;

import com.mycompany.serverside.dto.PlayerDto;
import data.DbManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDao {

    private static final String INSERT_PLAYER
            = "INSERT INTO PLAYER (NAME, EMAIL, PASSWORD, SCORE) "
            + "VALUES (?, ?, ?, ?)";

    private static final String LOGIN_QUERY
            = "SELECT * FROM PLAYER WHERE NAME = ? AND PASSWORD = ?";

    private static final String REGISTER_CHECK = "SELECT * FROM PLAYER WHERE EMAIL = ?";

    private static final String GET_ALL_PLAYERS_QUERY = "SELECT * FROM PLAYER ORDER BY SCORE DESC";

    private static final String INCREASE_WINNER_SCORE
            = "UPDATE PLAYER SET SCORE = SCORE + 1 WHERE ID = ?";

    public static PlayerDto register(
            String name,
            String email,
            String password
    ) {

        ResultSet rs = DbManager.init().getQueryPrepared(REGISTER_CHECK, email);
        System.out.println(rs);
        try {
            if (rs != null && rs.next()) {
                return null;
            }
        } catch (SQLException ex) {
            System.getLogger(PlayerDao.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        int id = DbManager.init().insertAndGetId(
                INSERT_PLAYER,
                name,
                email,
                password,
                0
        );

        if (id == -1) {
            return null;
        }

        PlayerDto p = new PlayerDto();
        p.setId(id);
        p.setName(name);
        p.setEmail(email);
        p.setScore(0);

        return p;
    }

    public static PlayerDto login(String username, String password) {
        try {
            ResultSet rs = DbManager.init().getQueryPrepared(LOGIN_QUERY, username, password);
            if (rs != null && rs.next()) {
                PlayerDto p = new PlayerDto();
                p.setId(rs.getInt("ID"));
                p.setName(rs.getString("NAME"));
                p.setEmail(rs.getString("EMAIL"));
                p.setPassword(rs.getString("PASSWORD"));
                p.setScore(rs.getInt("SCORE"));
                p.setGender(rs.getString("GENDER"));
                return p;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<PlayerDto> getAllPlayers() {
        List<PlayerDto> players = new ArrayList<>();

        try {
            ResultSet rs = DbManager.init().getQuery(GET_ALL_PLAYERS_QUERY);

            while (rs != null && rs.next()) {
                PlayerDto p = new PlayerDto();
                p.setId(rs.getInt("ID"));
                p.setName(rs.getString("NAME"));
                p.setEmail(rs.getString("EMAIL"));
                p.setPassword(rs.getString("PASSWORD"));
                p.setScore(rs.getInt("SCORE"));
                p.setGender(rs.getString("GENDER"));

                players.add(p);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return players;
    }

    public static boolean increaseWinnerScore(int playerId) {
        System.out.println("Increasing score for player ID: " + playerId);
        
        boolean result = DbManager.init().updateQueryPrepared(INCREASE_WINNER_SCORE, playerId);
        
        if (result) {
            System.out.println("Player ID " + playerId + " score increased successfully");
        } else {
            System.err.println("Failed to increase score for player ID " + playerId);
        }
        
        return result;
    }

}