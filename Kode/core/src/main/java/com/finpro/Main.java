package com.finpro;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.managers.GameStateManager;
import com.finpro.screens.MenuScreen;

public class Main extends Game {
    public static final int WORLD_WIDTH = 1600;
    public static final int WORLD_HEIGHT = 900;

    public SpriteBatch batch;
    private GameStateManager gameStateManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        gameStateManager = GameStateManager.getInstance();
        gameStateManager.setGame(this);

        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (gameStateManager != null) {
            gameStateManager.dispose();
        }
    }
}
