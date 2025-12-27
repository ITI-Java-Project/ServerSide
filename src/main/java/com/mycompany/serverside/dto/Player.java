package com.mycompany.serverside.dto;

public class Player {
    private final int id ;
    public int score;
    public String name , email , password , gender;

    public Player(int id, int score, String name, String email, String password, String gender) {
        this.id = id;
        this.score = score;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public int getId() {  return id; }
}