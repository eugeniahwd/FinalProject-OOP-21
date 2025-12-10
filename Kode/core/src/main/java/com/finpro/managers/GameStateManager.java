package com.finpro.managers;

import com.finpro.Main;
import com.finpro.screens.GameScreen;
import com.finpro.screens.MenuScreen;

public class GameStateManager {
    private static GameStateManager instance;
    private Main game;  // FIXED: Changed from Game to Main
    private int currentLevel = 1; // 1=Easy, 2=Medium, 3=Hard

    // Store username throughout game session
    private String player1Username;
    private String player2Username;

    private GameStateManager() {}

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    // FIXED: Changed parameter type from Game to Main
    public void setGame(Main game) {
        this.game = game;
    }

    // Set username saat game dimulai
    public void setPlayerUsernames(String player1, String player2) {
        this.player1Username = player1;
        this.player2Username = player2;
        System.out.println("GameStateManager: Usernames set - P1: " + player1 + ", P2: " + player2);
    }

    // Start level with current usernames
    public void startLevel(int level) {
        this.currentLevel = level;

        // Jika username belum diset, redirect ke menu
        if (player1Username == null || player2Username == null) {
            System.err.println("⚠ GameStateManager: Usernames not set! Redirecting to menu.");
            game.setScreen(new MenuScreen(game));
            return;
        }

        System.out.println("GameStateManager: Starting level " + level +
            " with " + player1Username + " & " + player2Username);
        game.setScreen(new GameScreen(game, level, player1Username, player2Username));
    }

    // Go to next level (for level progression)
    public void nextLevel() {
        currentLevel++;
        if (currentLevel <= 3) {
            System.out.println("GameStateManager: Advancing to level " + currentLevel);
            startLevel(currentLevel);
        } else {
            // All levels completed, back to menu
            System.out.println("GameStateManager: All levels completed! Back to menu.");
            game.setScreen(new MenuScreen(game));
            resetSession();
        }
    }

    // Reset session (clear usernames and level)
    public void resetSession() {
        System.out.println("GameStateManager: Resetting session");
        currentLevel = 1;
        player1Username = null;
        player2Username = null;
    }

    // Getters
    public int getCurrentLevel() {
        return currentLevel;
    }

    public String getPlayer1Username() {
        return player1Username;
    }

    public String getPlayer2Username() {
        return player2Username;
    }

    public boolean hasActiveSession() {
        return player1Username != null && player2Username != null;
    }

    public void dispose() {
        resetSession();
    }
}
