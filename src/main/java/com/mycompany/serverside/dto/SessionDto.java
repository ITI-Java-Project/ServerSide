package com.mycompany.serverside.dto;

public class SessionDto {

    private final int id;
    private final int player1Id;
    private final int player2Id;
    private int player1Score;
    private int player2Score;
    private final String player1Name;
    private final String player2Name;

    public SessionDto(int id, int player1Id, int player2Id, String player1Name, String player2Name ,int player1Score, int player2Score) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    public int getId() {
        return id;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }
}
