package com.finpro.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "game_sessions")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id")
    private UUID sessionId;

    // Player 1 (FireGirl - Red player)
    @Column(name = "player1_id", nullable = false)
    private UUID player1Id;

    @Column(name = "player1_username")
    private String player1Username;

    @Column(name = "player1_red_diamonds")
    private Integer player1RedDiamonds = 0;

    @Column(name = "player1_score")
    private Integer player1Score = 0;

    // Player 2 (WaterBoy - Blue player)
    @Column(name = "player2_id", nullable = false)
    private UUID player2Id;

    @Column(name = "player2_username")
    private String player2Username;

    @Column(name = "player2_blue_diamonds")
    private Integer player2BlueDiamonds = 0;

    @Column(name = "player2_score")
    private Integer player2Score = 0;

    // Game Stats
    @Column(name = "total_diamonds")
    private Integer totalDiamonds = 0;

    @Column(name = "total_score")
    private Integer totalScore = 0;

    @Column(name = "level_number")
    private Integer levelNumber;

    @Column(name = "keys_collected")
    private Integer keysCollected = 0;

    @Column(name = "total_keys")
    private Integer totalKeys = 0;

    @Column(name = "time_seconds")
    private Integer timeSeconds;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "winner_id")
    private UUID winnerId;

    @Column(name = "winner_username")
    private String winnerUsername;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    // Constructors
    public GameSession() {}

    public GameSession(UUID player1Id, String player1Username,
                       UUID player2Id, String player2Username, Integer levelNumber) {
        this.player1Id = player1Id;
        this.player1Username = player1Username;
        this.player2Id = player2Id;
        this.player2Username = player2Username;
        this.levelNumber = levelNumber;
    }

    // Getters and Setters
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }

    public UUID getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(UUID player1Id) { this.player1Id = player1Id; }

    public String getPlayer1Username() { return player1Username; }
    public void setPlayer1Username(String player1Username) {
        this.player1Username = player1Username;
    }

    public Integer getPlayer1RedDiamonds() { return player1RedDiamonds; }
    public void setPlayer1RedDiamonds(Integer player1RedDiamonds) {
        this.player1RedDiamonds = player1RedDiamonds;
    }

    public Integer getPlayer1Score() { return player1Score; }
    public void setPlayer1Score(Integer player1Score) { this.player1Score = player1Score; }

    public UUID getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(UUID player2Id) { this.player2Id = player2Id; }

    public String getPlayer2Username() { return player2Username; }
    public void setPlayer2Username(String player2Username) {
        this.player2Username = player2Username;
    }

    public Integer getPlayer2BlueDiamonds() { return player2BlueDiamonds; }
    public void setPlayer2BlueDiamonds(Integer player2BlueDiamonds) {
        this.player2BlueDiamonds = player2BlueDiamonds;
    }

    public Integer getPlayer2Score() { return player2Score; }
    public void setPlayer2Score(Integer player2Score) { this.player2Score = player2Score; }

    public Integer getTotalDiamonds() { return totalDiamonds; }
    public void setTotalDiamonds(Integer totalDiamonds) {
        this.totalDiamonds = totalDiamonds;
    }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public Integer getLevelNumber() { return levelNumber; }
    public void setLevelNumber(Integer levelNumber) { this.levelNumber = levelNumber; }

    public Integer getKeysCollected() { return keysCollected; }
    public void setKeysCollected(Integer keysCollected) {
        this.keysCollected = keysCollected;
    }

    public Integer getTotalKeys() { return totalKeys; }
    public void setTotalKeys(Integer totalKeys) { this.totalKeys = totalKeys; }

    public Integer getTimeSeconds() { return timeSeconds; }
    public void setTimeSeconds(Integer timeSeconds) { this.timeSeconds = timeSeconds; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public UUID getWinnerId() { return winnerId; }
    public void setWinnerId(UUID winnerId) { this.winnerId = winnerId; }

    public String getWinnerUsername() { return winnerUsername; }
    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    // Business Methods
    public void calculateTotals() {
        this.totalDiamonds = this.player1RedDiamonds + this.player2BlueDiamonds;
        this.totalScore = this.player1Score + this.player2Score;
    }

    public void determineWinner() {
        if (this.player1Score > this.player2Score) {
            this.winnerId = this.player1Id;
            this.winnerUsername = this.player1Username;
        } else if (this.player2Score > this.player1Score) {
            this.winnerId = this.player2Id;
            this.winnerUsername = this.player2Username;
        }
        // null jika draw
    }

    public void finishGame() {
        this.completed = true;
        this.finishedAt = LocalDateTime.now();
        calculateTotals();
        determineWinner();
    }
}