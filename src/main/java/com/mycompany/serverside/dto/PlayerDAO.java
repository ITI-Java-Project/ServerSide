package com.mycompany.serverside.dto;

import data.DbManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDAO {

    private static final String INSERT_PLAYER
            = "INSERT INTO PLAYER (NAME, EMAIL, PASSWORD, SCORE) "
            + "VALUES (?, ?, ?, ?)";

    private static final String LOGIN_QUERY
            = "SELECT * FROM PLAYER WHERE NAME = ? AND PASSWORD = ?";

    public static Player register(
            String name,
            String email,
            String password
    ) {

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

        Player p = new Player();
        p.setId(id);
        p.setName(name);
        p.setEmail(email);
        p.setScore(0);

        return p;
    }

    public static Player login(String username, String password){
        try {
            ResultSet rs = DbManager.init().getQueryPrepared(LOGIN_QUERY, username, password);
            if (rs != null && rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("ID"));
                p.setName(rs.getString("NAME"));
                p.setEmail(rs.getString("EMAIL"));
                p.setPassword(rs.getString("PASSWORD"));
                p.setScore(rs.getInt("SCORE"));
                p.setGender(rs.getString("GENDER"));
                return p;
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
