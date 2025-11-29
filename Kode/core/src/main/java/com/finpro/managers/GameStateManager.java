package com.finpro.managers;

import com.badlogic.gdx.Game;
import com.finpro.screens.GameScreen;
import com.finpro.screens.VictoryScreen;

public class GameStateManager {
    private static GameStateManager instance;
    private Game game;
    private int currentLevel = 1; // 1=Easy, 2=Medium, 3=Hard

    private GameStateManager() {}

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void startLevel(int level) {
        this.currentLevel = level;
        game.setScreen(new GameScreen(game, level));
    }

    public void nextLevel() {
        currentLevel++;
        if (currentLevel <= 3) {
            startLevel(currentLevel);
        } else {
            // All levels completed, Show victory screen
            game.setScreen(new VictoryScreen(game));
            currentLevel = 1; // Reset for next playthrough
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void dispose() {
        // Cleanup if needed
    }
}
