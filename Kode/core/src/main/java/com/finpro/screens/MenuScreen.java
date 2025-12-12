package com.finpro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.Main;
import com.finpro.managers.GameStateManager;

/**
 * Main Menu Screen
 */
public class MenuScreen implements Screen {
    private Main game;  // FIXED: Changed from Game to Main
    private OrthographicCamera camera;
    private BitmapFont font;
    private SpriteBatch batch;
    private float timer;

    // FIXED: Changed parameter type from Game to Main
    public MenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        font = new BitmapFont();
        font.getData().setScale(2);
        batch = new SpriteBatch();
        timer = 0;

        // ADDED: Reset session when returning to menu
        GameStateManager.getInstance().resetSession();
    }

    @Override
    public void render(float delta) {
        timer += delta;

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Animated title
        float pulse = (float) Math.abs(Math.sin(timer * 2)) * 0.5f + 1.5f;
        font.setColor(1, pulse, 0, 1);
        font.draw(batch, "SPARKBOUNDS", 500, 550);


        // Level selection
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, "PRESS 1 TO START", 500, 400);

        // Instructions
        font.getData().setScale(1f);
        font.setColor(Color.CYAN);

        // Controls reminder
        font.getData().setScale(1.5f);
        font.setColor(Color.GRAY);
        font.draw(batch, "FireGirl: WASD | WaterBoy: Arrow Keys | ESC: Exit", 350, 100);

        batch.end();

        font.getData().setScale(2f);

        // Handle input - Go to UsernameInputScreen instead of directly to game
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
            game.setScreen(new UsernameInputScreen(game, 1));
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
            game.setScreen(new UsernameInputScreen(game, 2));
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3)) {
            game.setScreen(new UsernameInputScreen(game, 3));
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
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
