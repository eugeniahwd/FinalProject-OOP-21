package com.finpro.backend.dto;

public class GameFinishRequest {
    private Integer player1RedDiamonds;
    private Integer player1Score;
    private Integer player2BlueDiamonds;
    private Integer player2Score;
    private Integer keysCollected;
    private Integer totalKeys;
    private Integer timeSeconds;

    // Constructors
    public GameFinishRequest() {}

    // Getters and Setters
    public Integer getPlayer1RedDiamonds() { return player1RedDiamonds; }
    public void setPlayer1RedDiamonds(Integer player1RedDiamonds) {
        this.player1RedDiamonds = player1RedDiamonds;
    }

    public Integer getPlayer1Score() { return player1Score; }
    public void setPlayer1Score(Integer player1Score) {
        this.player1Score = player1Score;
    }

    public Integer getPlayer2BlueDiamonds() { return player2BlueDiamonds; }
    public void setPlayer2BlueDiamonds(Integer player2BlueDiamonds) {
        this.player2BlueDiamonds = player2BlueDiamonds;
    }

    public Integer getPlayer2Score() { return player2Score; }
    public void setPlayer2Score(Integer player2Score) {
        this.player2Score = player2Score;
    }

    public Integer getKeysCollected() { return keysCollected; }
    public void setKeysCollected(Integer keysCollected) {
        this.keysCollected = keysCollected;
    }

    public Integer getTotalKeys() { return totalKeys; }
    public void setTotalKeys(Integer totalKeys) { this.totalKeys = totalKeys; }

    public Integer getTimeSeconds() { return timeSeconds; }
    public void setTimeSeconds(Integer timeSeconds) { this.timeSeconds = timeSeconds; }
}