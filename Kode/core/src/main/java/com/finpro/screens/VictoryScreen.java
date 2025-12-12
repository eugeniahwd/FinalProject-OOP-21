package com.finpro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.finpro.Main;
import com.finpro.facade.GameFacade;
import com.finpro.service.ApiService;

public class VictoryScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private float timer;
    private GameFacade gameFacade;
    private boolean dataSaved = false;
    private boolean savingData = false;

    private String player1Username;
    private String player2Username;

    // FIXED: Changed parameter type from Game to Main
    public VictoryScreen(Main game, GameFacade gameFacade, String player1Username, String player2Username) {
        this.game = game;
        this.gameFacade = gameFacade;
        this.player1Username = player1Username;
        this.player2Username = player2Username;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        font = new BitmapFont();
        timer = 0;

        saveGameResults();
    }

    private void saveGameResults() {
        savingData = true;
        ApiService api = ApiService.getInstance();

        System.out.println("=== Saving Game Results ===");
        System.out.println("Player 1: " + player1Username);
        System.out.println("Player 2: " + player2Username);
        System.out.println("Session ID: " + api.getCurrentSessionId());

        // VERIFY SESSION EXISTS
        if (api.getCurrentSessionId() == null) {
            System.err.println("✗ ERROR: No session ID! Cannot save results.");
            savingData = false;
            return;
        }

        api.finishGame(
            gameFacade.getFireGirlRedDiamonds(),
            gameFacade.getFireGirlScore(),
            gameFacade.getWaterBoyBlueDiamonds(),
            gameFacade.getWaterBoyScore(),
            gameFacade.getKeysCollected(),
            gameFacade.getTotalKeys(),
            gameFacade.getGameTimeSeconds(),
            new ApiService.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    System.out.println("✓ Game results saved to database!");
                    System.out.println("Response: " + response);
                    dataSaved = true;
                    savingData = false;
                }

                @Override
                public void onError(String error) {
                    System.err.println("✗ Failed to save: " + error);
                    System.out.println("⚠ Game completed (offline mode)");
                    savingData = false;
                }
            }
        );
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
        font.draw(game.batch, "VICTORY!", 450, 650);

        // Congratulations
        font.getData().setScale(2f);
        font.setColor(1, colorWave, 0, 1);
        font.draw(game.batch, "Level Complete!", 480, 570);

        // Game Statistics
        font.getData().setScale(1.3f);
        font.setColor(Color.WHITE);
        font.draw(game.batch, "Game Statistics:", 500, 500);

        font.getData().setScale(1.1f);
        font.setColor(Color.RED);
        font.draw(game.batch, "FireGirl - Red Diamonds: " + gameFacade.getFireGirlRedDiamonds(), 420, 460);
        font.draw(game.batch, "FireGirl - Score: " + gameFacade.getFireGirlScore(), 420, 430);

        font.setColor(Color.CYAN);
        font.draw(game.batch, "WaterBoy - Blue Diamonds: " + gameFacade.getWaterBoyBlueDiamonds(), 420, 390);
        font.draw(game.batch, "WaterBoy - Score: " + gameFacade.getWaterBoyScore(), 420, 360);

        font.setColor(Color.YELLOW);
        int totalScore = gameFacade.getFireGirlScore() + gameFacade.getWaterBoyScore();
        font.draw(game.batch, "Total Score: " + totalScore, 480, 320);
        font.draw(game.batch, "Time: " + gameFacade.getGameTimeSeconds() + " seconds", 490, 290);

        // Save status
        font.getData().setScale(0.9f);
        if (dataSaved) {
            font.setColor(Color.GREEN);
            font.draw(game.batch, "✓ Results saved to database!", 490, 250);
        } else if (savingData) {
            font.setColor(Color.ORANGE);
            font.draw(game.batch, "Saving to backend...", 520, 250);
        } else {
            font.setColor(Color.GRAY);
            font.draw(game.batch, "⚠ Offline mode (backend not connected)", 450, 250);
        }

        // Instructions
        font.getData().setScale(1.1f);
        font.setColor(Color.CYAN);

        int currentLevel = com.finpro.managers.GameStateManager.getInstance().getCurrentLevel();
        if (currentLevel < 3) {
            font.draw(game.batch, "Press ENTER for Next Level", 460, 180);
        } else {
            font.draw(game.batch, "Press ENTER to Main Menu", 465, 180);
        }

        font.draw(game.batch, "Press M for Main Menu", 475, 150);
        font.draw(game.batch, "Press ESC to exit", 500, 120);

        // Credits
        font.getData().setScale(0.7f);
        font.setColor(Color.GRAY);
        font.draw(game.batch, "FireGirl & WaterBoy | LibGDX + Spring Boot + PostgreSQL", 390, 50);

        game.batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            gameFacade.dispose();

            // Check if there's a next level
            if (currentLevel < 3) {
                // Go to next level
                com.finpro.managers.GameStateManager.getInstance().nextLevel();
            } else {
                // All levels completed, back to menu
                com.finpro.managers.GameStateManager.getInstance().resetSession();
                game.setScreen(new MenuScreen(game));
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            gameFacade.dispose();
            com.finpro.managers.GameStateManager.getInstance().resetSession();
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
