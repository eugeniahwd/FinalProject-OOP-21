package com.finpro.managers;

import com.finpro.Main;
import com.finpro.screens.GameScreen;
import com.finpro.screens.MenuScreen;

public class GameStateManager {
    private static GameStateManager instance;
    private Main game;
    private int currentLevel = 1;

    private String player1Username;
    private String player2Username;

    private GameStateManager() {}

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public void setGame(Main game) {
        this.game = game;
    }

    // set username pas start
    public void setPlayerUsernames(String player1, String player2) {
        this.player1Username = player1;
        this.player2Username = player2;
        System.out.println("GameStateManager: Usernames set - P1: " + player1 + ", P2: " + player2);
    }

    public void startLevel(int level) {
        this.currentLevel = level;

        // kalo username belom di set, balik ke menu
        if (player1Username == null || player2Username == null) {
            System.err.println("⚠ GameStateManager: Usernames not set! Redirecting to menu.");
            game.setScreen(new MenuScreen(game));
            return;
        }

        System.out.println("GameStateManager: Starting level " + level +
            " with " + player1Username + " & " + player2Username);
        game.setScreen(new GameScreen(game, level, player1Username, player2Username));
    }

    // ke next level
    public void nextLevel() {
        currentLevel++;
        if (currentLevel <= 3) {
            System.out.println("GameStateManager: Advancing to level " + currentLevel);
            startLevel(currentLevel);
        } else {
            // level udah complete, balik ke menu
            System.out.println("GameStateManager: All levels completed! Back to menu.");
            game.setScreen(new MenuScreen(game));
            resetSession();
        }
    }

    // reset username dan level
    public void resetSession() {
        System.out.println("GameStateManager: Resetting session");
        currentLevel = 1;
        player1Username = null;
        player2Username = null;
    }

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
