package com.mycompany.serverside.dto;

import java.security.Timestamp;

public class Session {

    private final int id;
    private final int player1_id;
    private final int player2_id;
    public int winner_id;
    public Timestamp startTime , endTime; 

    public Session(int id, int player1_id, int player2_id, int winner_id, Timestamp startTime, Timestamp endTime) {
        this.id = id;
        this.player1_id = player1_id;
        this.player2_id = player2_id;
        this.winner_id = winner_id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() { return id; }

    public int getPlayer1_id() { return player1_id;  }

    public int getPlayer2_id() {   return player2_id; }
}