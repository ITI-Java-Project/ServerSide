package com.mycompany.serverside.dto;

public class Player {

    private int id;
    private int score;
    private String name;
    private String email;
    private String password;
    private String gender;

    public Player(int id, int score, String name, String email, String password, String gender) {
        this.id = id;
        this.score = score;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public Player(int score, String name) {
        this.score = score;
        this.name = name;
    }

    public Player() {
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Player{"
                + "id=" + id
                + ", score=" + score
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + ", password='" + password + '\''
                + ", gender='" + gender + '\''
                + '}';
    }
}
