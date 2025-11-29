package com.finpro.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.managers.GameStateManager;

public class GameOverScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private SpriteBatch batch;
    private int currentLevel;
    private float timer;

    public GameOverScreen(Game game, int level) {
        this.game = game;
        this.currentLevel = level;

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

        // Tips
        font.getData().setScale(1.2f);
        font.setColor(Color.CYAN);
        font.draw(batch, "REMEMBER:", 550, 170);
        font.getData().setScale(1f);
        font.setColor(Color.RED);
        font.draw(batch, "• FireGirl (Red) dies in WATER", 450, 140);
        font.setColor(Color.BLUE);
        font.draw(batch, "• WaterBoy (Blue) dies in LAVA", 450, 115);
        font.setColor(Color.GREEN);
        font.draw(batch, "• Push boxes to create safe paths", 440, 90);
        font.draw(batch, "• Find all keys to unlock the door", 435, 65);

        batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            GameStateManager.getInstance().startLevel(currentLevel);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
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
