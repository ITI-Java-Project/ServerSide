package com.mycompany.serverside.dto;

public class Player {
    private final int id ;
    private int score;
    private String name , email , password , gender;

    public Player(int id, int score, String name, String email, String password, String gender) {
        this.id = id;
        this.score = score;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public int getId() {  return id; }
}