package com.finpro.backend.dto;

public class LeaderboardEntry {
    private int rank;
    private String username;
    private Integer score;
    private Integer totalDiamonds;
    private Integer wins;
    private Integer levelsCompleted;

    // Constructors
    public LeaderboardEntry() {}

    public LeaderboardEntry(int rank, String username) {
        this.rank = rank;
        this.username = username;
    }

    // Getters and Setters
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalDiamonds() {
        return totalDiamonds;
    }

    public void setTotalDiamonds(Integer totalDiamonds) {
        this.totalDiamonds = totalDiamonds;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLevelsCompleted() {
        return levelsCompleted;
    }

    public void setLevelsCompleted(Integer levelsCompleted) {
        this.levelsCompleted = levelsCompleted;
    }
}