package com.finpro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import com.finpro.Main;
import com.finpro.facade.GameFacade;

public class GameScreen implements Screen {
    private Main game;  // FIXED: Changed from Game to Main
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch batch;
    private SpriteBatch uiBatch;

    private GameFacade gameFacade;
    private int level;
    private boolean levelCompleteTriggered;

    private String player1Username;
    private String player2Username;

    // FIXED: Changed parameter type from Game to Main
    public GameScreen(Main game, int level, String player1Username, String player2Username) {
        this.game = game;
        this.level = level;
        this.levelCompleteTriggered = false;
        this.player1Username = player1Username;
        this.player2Username = player2Username;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        gameFacade = new GameFacade(level);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();
        gameFacade.updateGame(delta);
        updateCamera();

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        gameFacade.renderGame(batch, shapeRenderer);
        renderUI();

        if (gameFacade.isGameOver()) {
            game.setScreen(new GameOverScreen(game, level, player1Username, player2Username));
        }

        // FIXED: Pass Main instead of Game
        if (!levelCompleteTriggered && gameFacade.checkLevelComplete()) {
            levelCompleteTriggered = true;
            game.setScreen(new VictoryScreen(game, gameFacade, player1Username, player2Username));
        }
    }

    private void updateCamera() {
        // Camera follows the midpoint between both players
        float fireGirlX = gameFacade.getFireGirl().getX();
        float waterBoyX = gameFacade.getWaterBoy().getX();
        float fireGirlY = gameFacade.getFireGirl().getY();
        float waterBoyY = gameFacade.getWaterBoy().getY();

        float targetX = (fireGirlX + waterBoyX) / 2 + 20;
        float targetY = (fireGirlY + waterBoyY) / 2 + 25;

        // Smooth camera movement
        float lerpSpeed = 0.1f;
        camera.position.x += (targetX - camera.position.x) * lerpSpeed;
        camera.position.y += (targetY - camera.position.y) * lerpSpeed;

        // Clamp camera to world bounds
        float halfWidth = camera.viewportWidth / 2;
        float halfHeight = camera.viewportHeight / 2;

        camera.position.x = MathUtils.clamp(camera.position.x, halfWidth, Main.WORLD_WIDTH - halfWidth);
        camera.position.y = MathUtils.clamp(camera.position.y, halfHeight, Main.WORLD_HEIGHT - halfHeight);
    }

    private void renderUI() {
        uiBatch.begin();

        // Level info
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "Level " + level, 20, Gdx.graphics.getHeight() - 20);

        // timer
        font.setColor(Color.YELLOW);
        int minutes = gameFacade.getGameTimeSeconds() / 60;
        int seconds = gameFacade.getGameTimeSeconds() % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);
        font.draw(uiBatch, timeText, 20, Gdx.graphics.getHeight() - 50);

        // Keys collected
        String keyText = "Keys: " + gameFacade.getKeysCollected() + "/" + gameFacade.getTotalKeys();
        font.setColor(gameFacade.isDoorUnlocked() ? Color.GREEN : Color.YELLOW);
        font.draw(uiBatch, keyText, 20, Gdx.graphics.getHeight() - 80);

        // Door status
        if (gameFacade.isDoorUnlocked()) {
            font.setColor(Color.GREEN);
            font.draw(uiBatch, "Door UNLOCKED!", 20, Gdx.graphics.getHeight() - 110);
        } else {
            font.setColor(Color.RED);
            font.draw(uiBatch, "Door LOCKED - Find Keys!", 20, Gdx.graphics.getHeight() - 110);
        }

        font.getData().setScale(1.2f);
        uiBatch.end();
    }

    private void handleInput() {
        // FireGirl controls (WASD)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            gameFacade.getFireGirl().moveLeft();
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            gameFacade.getFireGirl().moveRight();
        } else {
            gameFacade.getFireGirl().stopMoving();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            gameFacade.getFireGirl().jump();
        }

        // WaterBoy controls (Arrow Keys)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            gameFacade.getWaterBoy().moveLeft();
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            gameFacade.getWaterBoy().moveRight();
        } else {
            gameFacade.getWaterBoy().stopMoving();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            gameFacade.getWaterBoy().jump();
        }

        // Quick restart
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            // Use GameStateManager for restart
            com.finpro.managers.GameStateManager gsm = com.finpro.managers.GameStateManager.getInstance();
            if (gsm.hasActiveSession()) {
                gsm.startLevel(level);
            } else {
                // Fallback if session lost
                game.setScreen(new GameScreen(game, level, player1Username, player2Username));
            }
        }

        // Back to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            com.finpro.managers.GameStateManager.getInstance().resetSession();
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        batch.dispose();
        uiBatch.dispose();
        gameFacade.dispose();
    }
}
