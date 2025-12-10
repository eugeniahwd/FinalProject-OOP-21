package com.finpro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.Main;

public class GameOverScreen implements Screen {
    private Main game;  // FIXED: Changed from Game to Main
    private OrthographicCamera camera;
    private BitmapFont font;
    private SpriteBatch batch;
    private int currentLevel;
    private float timer;
    private String player1Username;
    private String player2Username;

    // FIXED: Changed parameter type from Game to Main
    public GameOverScreen(Main game, int level, String p1, String p2) {
        this.game = game;
        this.currentLevel = level;
        this.player1Username = p1;
        this.player2Username = p2;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        font = new BitmapFont();
        batch = new SpriteBatch();
        timer = 0;
    }

    @Override
    public void render(float delta) {
        timer += delta;

        Gdx.gl.glClearColor(0.1f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Pulsing effect for GAME OVER text
        float pulse = (float) Math.abs(Math.sin(timer * 3));

        // GAME OVER text
        font.getData().setScale(4f);
        font.setColor(1, pulse * 0.3f, 0, 1);
        font.draw(batch, "GAME OVER!", 420, 500);

        // Death message
        font.getData().setScale(1.8f);
        font.setColor(Color.WHITE);
        font.draw(batch, "A player touched the wrong element!", 320, 410);

        // Instructions
        font.getData().setScale(1.5f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "Press R to Retry Level " + currentLevel, 410, 320);
        font.draw(batch, "Press M for Main Menu", 440, 280);
        font.draw(batch, "Press ESC to Exit", 470, 240);

        batch.end();

        // Handle input - Use GameStateManager for retry
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            // Retry current level using GameStateManager
            com.finpro.managers.GameStateManager gsm = com.finpro.managers.GameStateManager.getInstance();
            if (gsm.hasActiveSession()) {
                gsm.startLevel(currentLevel);
            } else {
                // Fallback if session lost
                game.setScreen(new GameScreen(game, currentLevel, player1Username, player2Username));
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            com.finpro.managers.GameStateManager.getInstance().resetSession();
            game.setScreen(new MenuScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        // Reset font scale
        font.getData().setScale(1f);
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}
