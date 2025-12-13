package com.finpro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.finpro.Main;
import com.finpro.facade.GameFacade;
import com.finpro.service.ApiService;

public class VictoryScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout layout;
    private float timer;
    private GameFacade gameFacade;
    private boolean dataSaved = false;
    private boolean savingData = false;

    private String player1Username;
    private String player2Username;

    public VictoryScreen(Main game, GameFacade gameFacade, String player1Username, String player2Username) {
        this.game = game;
        this.gameFacade = gameFacade;
        this.player1Username = player1Username;
        this.player2Username = player2Username;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        font = new BitmapFont();
        layout = new GlyphLayout();
        timer = 0;

        saveGameResults();
    }

    private void drawCenteredText(String text, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        layout.setText(font, text);
        float x = (1280 - layout.width) / 2;
        font.draw(game.batch, text, x, y);
    }

    private void saveGameResults() {
        savingData = true;
        ApiService api = ApiService.getInstance();

        System.out.println("=== Saving Game Results ===");
        System.out.println("Player 1: " + player1Username);
        System.out.println("Player 2: " + player2Username);
        System.out.println("Session ID: " + api.getCurrentSessionId());

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
        float rainbowR = (float) Math.sin(timer) * 0.5f + 0.5f;
        float rainbowG = (float) Math.sin(timer + 2) * 0.5f + 0.5f;
        float rainbowB = (float) Math.sin(timer + 4) * 0.5f + 0.5f;
        float pulse = (float) Math.sin(timer * 2) * 0.5f + 0.5f;

        // Victory message
        drawCenteredText("VICTORY!", 650, 4f, new Color(rainbowR, rainbowG, rainbowB, 1));

        // Congratulations
        drawCenteredText("Level Complete!", 570, 2f, new Color(1, pulse, 0, 1));

        // Divider line
        drawCenteredText("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", 530, 1f, Color.GRAY);

        // Game Statistics Title
        drawCenteredText("Game Statistics", 490, 1.5f, Color.WHITE);

        // FireGirl Stats
        drawCenteredText("FireGirl - Red Diamonds: " + gameFacade.getFireGirlRedDiamonds(),
            445, 1.2f, Color.RED);
        drawCenteredText("FireGirl - Score: " + gameFacade.getFireGirlScore(),
            415, 1.2f, new Color(1f, 0.5f, 0.5f, 1f));

        // WaterBoy Stats
        drawCenteredText("WaterBoy - Blue Diamonds: " + gameFacade.getWaterBoyBlueDiamonds(),
            375, 1.2f, Color.CYAN);
        drawCenteredText("WaterBoy - Score: " + gameFacade.getWaterBoyScore(),
            345, 1.2f, new Color(0.5f, 0.8f, 1f, 1f));

        // Divider
        drawCenteredText("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", 315, 1f, Color.GRAY);

        // Total Stats
        int totalScore = gameFacade.getFireGirlScore() + gameFacade.getWaterBoyScore();
        drawCenteredText("Total Score: " + totalScore, 280, 1.3f, Color.YELLOW);
        drawCenteredText("Time: " + gameFacade.getGameTimeSeconds() + " seconds", 250, 1.1f, Color.YELLOW);

        // Save status
        if (dataSaved) {
            drawCenteredText("✓ Results saved to database!", 210, 1f, Color.GREEN);
        } else if (savingData) {
            drawCenteredText("Saving to backend...", 210, 1f, Color.ORANGE);
        } else {
            drawCenteredText("⚠ Offline mode (backend not connected)", 210, 0.9f, Color.GRAY);
        }

        // Instructions
        int currentLevel = com.finpro.managers.GameStateManager.getInstance().getCurrentLevel();
        if (currentLevel < 3) {
            drawCenteredText("Press ENTER for Next Level", 160, 1.2f, Color.CYAN);
        } else {
            drawCenteredText("Press ENTER to Main Menu", 160, 1.2f, Color.CYAN);
        }

        drawCenteredText("Press M for Main Menu  |  Press ESC to Exit", 120, 1f, Color.LIGHT_GRAY);

        // Credits
        drawCenteredText("LibGDX + Spring Boot + PostgreSQL", 60, 0.8f, new Color(0.5f, 0.5f, 0.5f, 1f));

        game.batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            gameFacade.dispose();

            if (currentLevel < 3) {
                com.finpro.managers.GameStateManager.getInstance().nextLevel();
            } else {
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
