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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.finpro.Main;
import com.finpro.facade.GameFacade;
import com.finpro.managers.GameStateManager;

public class GameScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch batch;
    private SpriteBatch uiBatch; // Separate batch for UI

    private GameFacade gameFacade;
    private int level;
    private boolean levelCompleteTriggered;

    public GameScreen(Game game, int level) {
        this.game = game;
        this.level = level;
        this.levelCompleteTriggered = false;

        // Camera for game world
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720); // Viewport size

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch(); // UI always in screen space

        gameFacade = new GameFacade(level);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input
        handleInput();

        // Update game
        gameFacade.updateGame(delta);

        // Update camera to follow players
        updateCamera();

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // Render game world
        gameFacade.renderGame(batch, shapeRenderer);

        // Render UI (fixed to screen)
        renderUI();

        // Check game over
        if (gameFacade.isGameOver()) {
            game.setScreen(new GameOverScreen(game, level));
        }

        // Check level complete
        if (!levelCompleteTriggered && gameFacade.checkLevelComplete()) {
            levelCompleteTriggered = true;
            GameStateManager.getInstance().nextLevel();
        }
    }

    private void updateCamera() {
        // Camera follows the midpoint between both players
        float fireGirlX = gameFacade.getFireGirl().getX();
        float waterBoyX = gameFacade.getWaterBoy().getX();
        float fireGirlY = gameFacade.getFireGirl().getY();
        float waterBoyY = gameFacade.getWaterBoy().getY();

        float targetX = (fireGirlX + waterBoyX) / 2 + 20; // +20 for character width
        float targetY = (fireGirlY + waterBoyY) / 2 + 25; // +25 for character height

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

        // Keys collected
        String keyText = "Keys: " + gameFacade.getKeysCollected() + "/" + gameFacade.getTotalKeys();
        font.setColor(gameFacade.isDoorUnlocked() ? Color.GREEN : Color.YELLOW);
        font.draw(uiBatch, keyText, 20, Gdx.graphics.getHeight() - 50);

        // Door status
        if (gameFacade.isDoorUnlocked()) {
            font.setColor(Color.GREEN);
            font.draw(uiBatch, "Door UNLOCKED!", 20, Gdx.graphics.getHeight() - 80);
        } else {
            font.setColor(Color.RED);
            font.draw(uiBatch, "Door LOCKED - Find Keys!", 20, Gdx.graphics.getHeight() - 80);
        }

        // Controls
        font.setColor(Color.CYAN);
        font.getData().setScale(0.9f);
        font.draw(uiBatch, "WASD - FireGirl (Red) | Arrows - WaterBoy (Blue)", 20, Gdx.graphics.getHeight() - 120);
        font.draw(uiBatch, "Push boxes to create bridges | Collect all diamonds | Find keys to unlock door!", 20, Gdx.graphics.getHeight() - 145);
        font.draw(uiBatch, "RED dies in WATER | BLUE dies in LAVA", 20, Gdx.graphics.getHeight() - 170);
        font.draw(uiBatch, "Press R to restart", 20, Gdx.graphics.getHeight() - 195);

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
            game.setScreen(new GameScreen(game, level));
        }

        // Back to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
