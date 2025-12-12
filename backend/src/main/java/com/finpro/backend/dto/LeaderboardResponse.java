package com.finpro.backend.dto;

import java.util.List;

public class LeaderboardResponse {
    private String leaderboardType;
    private List<LeaderboardEntry> entries;
    private int totalEntries;

    // Constructors
    public LeaderboardResponse() {}

    public LeaderboardResponse(String leaderboardType, List<LeaderboardEntry> entries) {
        this.leaderboardType = leaderboardType;
        this.entries = entries;
        this.totalEntries = entries.size();
    }

    // Getters and Setters
    public String getLeaderboardType() {
        return leaderboardType;
    }

    public void setLeaderboardType(String leaderboardType) {
        this.leaderboardType = leaderboardType;
    }

    public List<LeaderboardEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LeaderboardEntry> entries) {
        this.entries = entries;
        this.totalEntries = entries.size();
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }
}