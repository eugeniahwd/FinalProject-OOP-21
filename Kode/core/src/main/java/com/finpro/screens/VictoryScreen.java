package com.finpro.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.finpro.Main;

public class VictoryScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private float timer;

    public VictoryScreen(Game game) {
        this.game = (Main) game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        font = new BitmapFont();
        timer = 0;
    }

    @Override
    public void render(float delta) {
        timer += delta;

        Gdx.gl.glClearColor(0.1f, 0.05f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // Animated colors
        float colorWave = (float) Math.sin(timer * 2) * 0.5f + 0.5f;
        float rainbowR = (float) Math.sin(timer) * 0.5f + 0.5f;
        float rainbowG = (float) Math.sin(timer + 2) * 0.5f + 0.5f;
        float rainbowB = (float) Math.sin(timer + 4) * 0.5f + 0.5f;

        // Victory message
        font.getData().setScale(4f);
        font.setColor(rainbowR, rainbowG, rainbowB, 1);
        font.draw(game.batch, "VICTORY!", 450, 550);

        // Congratulations
        font.getData().setScale(2f);
        font.setColor(1, colorWave, 0, 1);
        font.draw(game.batch, "Congratulations!", 460, 470);

        // Message
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "FireGirl & WaterBoy have conquered", 350, 400);
        font.draw(game.batch, "all three epic levels together!", 370, 370);

        // Achievement list
        font.getData().setScale(1.2f);
        font.setColor(Color.YELLOW);
        font.draw(game.batch, "ACHIEVEMENTS UNLOCKED:", 440, 310);

        font.getData().setScale(1f);
        font.setColor(Color.GREEN);
        font.draw(game.batch, "✓ All Diamonds Collected!", 470, 275);
        font.draw(game.batch, "✓ All Keys Found!", 500, 250);
        font.draw(game.batch, "✓ All Doors Unlocked!", 480, 225);
        font.draw(game.batch, "✓ Perfect Teamwork!", 490, 200);
        font.draw(game.batch, "✓ Master Puzzle Solver!", 470, 175);

        // Instructions
        font.getData().setScale(1.1f);
        font.setColor(Color.CYAN);
        font.draw(game.batch, "Press ENTER to return to menu", 410, 110);
        font.draw(game.batch, "Press ESC to exit", 490, 80);

        // Credits
        font.getData().setScale(0.8f);
        font.setColor(Color.GRAY);
        font.draw(game.batch, "Created with LibGDX | Design Patterns Project", 420, 35);
        font.draw(game.batch, "Featuring: Singleton, Factory, Facade, State, Object Pool", 360, 15);

        game.batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MenuScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

        // Reset scale
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
    }
}
