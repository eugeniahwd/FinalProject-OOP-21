package com.finpro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.Main;

public class GameOverScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private SpriteBatch batch;
    private int currentLevel;
    private float timer;
    private String player1Username;
    private String player2Username;
    private GlyphLayout layout;

    public GameOverScreen(Main game, int level, String p1, String p2) {
        this.game = game;
        this.currentLevel = level;
        this.player1Username = p1;
        this.player2Username = p2;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        font = new BitmapFont();
        batch = new SpriteBatch();
        layout = new GlyphLayout();
        timer = 0;
    }

    private void drawCenteredText(String text, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        layout.setText(font, text);
        float x = (1280 - layout.width) / 2;
        font.draw(batch, text, x, y);
    }

    @Override
    public void render(float delta) {
        timer += delta;

        Gdx.gl.glClearColor(0.1f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        float pulse = (float) Math.abs(Math.sin(timer * 3)); // efek untuk font

        Color gameOverColor = new Color(1, pulse * 0.3f, 0, 1);
        drawCenteredText("GAME OVER!", 500, 4f, gameOverColor);

        drawCenteredText("A player touched the wrong element!", 410, 1.8f, Color.WHITE);

        drawCenteredText("Press R to Retry Level " + currentLevel, 320, 1.5f, Color.YELLOW);
        drawCenteredText("Press M for Main Menu", 280, 1.5f, Color.YELLOW);
        drawCenteredText("Press ESC to Exit", 240, 1.5f, Color.YELLOW);

        batch.end();
        // input
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            com.finpro.managers.GameStateManager gsm = com.finpro.managers.GameStateManager.getInstance();
            if (gsm.hasActiveSession()) {
                gsm.startLevel(currentLevel);
            } else {
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
