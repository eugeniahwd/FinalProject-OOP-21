package com.finpro.backend.dto;

import java.util.UUID;

public class GameStartRequest {
    private String player1Username;
    private String player2Username;
    private Integer levelNumber;

    // Constructors
    public GameStartRequest() {}

    public GameStartRequest(String player1Username, String player2Username, Integer levelNumber) {
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.levelNumber = levelNumber;
    }

    // Getters and Setters
    public String getPlayer1Username() { return player1Username; }
    public void setPlayer1Username(String player1Username) {
        this.player1Username = player1Username;
    }

    public String getPlayer2Username() { return player2Username; }
    public void setPlayer2Username(String player2Username) {
        this.player2Username = player2Username;
    }

    public Integer getLevelNumber() { return levelNumber; }
    public void setLevelNumber(Integer levelNumber) { this.levelNumber = levelNumber; }
}
