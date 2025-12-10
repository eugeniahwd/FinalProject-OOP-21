package com.finpro.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "player_id")
    private UUID playerId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "high_score")
    private Integer highScore = 0;

    @Column(name = "total_red_diamonds")
    private Integer totalRedDiamonds = 0;

    @Column(name = "total_blue_diamonds")
    private Integer totalBlueDiamonds = 0;

    @Column(name = "total_games_played")
    private Integer totalGamesPlayed = 0;

    @Column(name = "total_wins")
    private Integer totalWins = 0;

    @Column(name = "levels_completed")
    private Integer levelsCompleted = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_played")
    private LocalDateTime lastPlayed;

    // Constructors
    public Player() {}

    public Player(String username) {
        this.username = username;
        this.lastPlayed = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getHighScore() { return highScore; }
    public void setHighScore(Integer highScore) { this.highScore = highScore; }

    public Integer getTotalRedDiamonds() { return totalRedDiamonds; }
    public void setTotalRedDiamonds(Integer totalRedDiamonds) {
        this.totalRedDiamonds = totalRedDiamonds;
    }

    public Integer getTotalBlueDiamonds() { return totalBlueDiamonds; }
    public void setTotalBlueDiamonds(Integer totalBlueDiamonds) {
        this.totalBlueDiamonds = totalBlueDiamonds;
    }

    public Integer getTotalGamesPlayed() { return totalGamesPlayed; }
    public void setTotalGamesPlayed(Integer totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public Integer getTotalWins() { return totalWins; }
    public void setTotalWins(Integer totalWins) { this.totalWins = totalWins; }

    public Integer getLevelsCompleted() { return levelsCompleted; }
    public void setLevelsCompleted(Integer levelsCompleted) {
        this.levelsCompleted = levelsCompleted;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastPlayed() { return lastPlayed; }
    public void setLastPlayed(LocalDateTime lastPlayed) { this.lastPlayed = lastPlayed; }

    // Business Methods
    public void updateHighScore(Integer newScore) {
        if (newScore > this.highScore) {
            this.highScore = newScore;
        }
    }

    public void addRedDiamonds(Integer count) {
        this.totalRedDiamonds += count;
    }

    public void addBlueDiamonds(Integer count) {
        this.totalBlueDiamonds += count;
    }

    public void incrementGamesPlayed() {
        this.totalGamesPlayed++;
    }

    public void incrementWins() {
        this.totalWins++;
    }

    public void updateLevelsCompleted(Integer level) {
        if (level > this.levelsCompleted) {
            this.levelsCompleted = level;
        }
    }

    public void updateLastPlayed() {
        this.lastPlayed = LocalDateTime.now();
    }
}