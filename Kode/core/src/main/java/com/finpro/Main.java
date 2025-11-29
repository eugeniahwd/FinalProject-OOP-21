package com.finpro;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.managers.GameStateManager;
import com.finpro.screens.MenuScreen;

public class Main extends Game {
    // Larger game world
    public static final float WORLD_WIDTH = 1600;
    public static final float WORLD_HEIGHT = 900;

    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Initialize GameStateManager (Singleton)
        GameStateManager.getInstance().setGame(this);

        // Start with menu screen
        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        // GAME LOOP pattern - dipanggil setiap frame
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        GameStateManager.getInstance().dispose();
    }
}
