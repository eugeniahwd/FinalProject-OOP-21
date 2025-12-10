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
import com.finpro.service.ApiService;
import java.util.UUID;

public class UsernameInputScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private SpriteBatch batch;

    private String player1Name = "";
    private String player2Name = "";
    private boolean isPlayer1Active = true;
    private int selectedLevel;
    private float blinkTimer = 0;
    private boolean testingConnection = false;

    public UsernameInputScreen(Main game, int level) {
        this.game = game;
        this.selectedLevel = level;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        font = new BitmapFont();
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        blinkTimer += delta;

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Title
        font.getData().setScale(2f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "Enter Player Names", 440, 620);

        // Level info
        font.getData().setScale(1.2f);
        font.setColor(Color.CYAN);
        String levelText = "Level " + selectedLevel + " - " +
            (selectedLevel == 1 ? "Easy" : selectedLevel == 2 ? "Medium" : "Hard");
        font.draw(batch, levelText, 520, 560);

        // Player 1 input
        font.getData().setScale(1.5f);
        font.setColor(isPlayer1Active ? Color.RED : Color.GRAY);
        font.draw(batch, "FireGirl (Player 1):", 350, 460);

        font.setColor(Color.WHITE);
        String p1Display = player1Name.isEmpty() ? "_" : player1Name;
        if (isPlayer1Active && blinkTimer % 1.0f < 0.5f) {
            p1Display += "|";
        }
        font.draw(batch, p1Display, 400, 410);

        // Player 2 input
        font.setColor(!isPlayer1Active ? Color.BLUE : Color.GRAY);
        font.draw(batch, "WaterBoy (Player 2):", 350, 330);

        font.setColor(Color.WHITE);
        String p2Display = player2Name.isEmpty() ? "_" : player2Name;
        if (!isPlayer1Active && blinkTimer % 1.0f < 0.5f) {
            p2Display += "|";
        }
        font.draw(batch, p2Display, 400, 280);

        // Instructions
        font.getData().setScale(1f);
        font.setColor(Color.YELLOW);
        if (isPlayer1Active) {
            font.draw(batch, "Type FireGirl's name, then press ENTER", 390, 210);
        } else {
            font.draw(batch, "Type WaterBoy's name, then press ENTER", 380, 210);
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "Press ESC to go back", 520, 170);

        // Connection status
        if (testingConnection) {
            font.setColor(Color.ORANGE);
            font.draw(batch, "Testing connection to server...", 450, 140);
        }

        // Show when both ready
        if (!player1Name.isEmpty() && !player2Name.isEmpty()) {
            font.getData().setScale(1.2f);
            font.setColor(Color.GREEN);
            font.draw(batch, "Press SPACE to START!", 480, 120);
        }

        batch.end();

        // Handle input (jika tidak sedang test connection)
        if (!testingConnection) {
            handleInput();
        }

        // Reset font
        font.getData().setScale(1f);
    }

    private void handleInput() {
        // ESC - back to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        // ENTER - switch to next player or start
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (isPlayer1Active && !player1Name.isEmpty()) {
                isPlayer1Active = false;
            } else if (!isPlayer1Active && !player2Name.isEmpty()) {
                startGame();
            }
            return;
        }

        // SPACE - start if both names entered
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (!player1Name.isEmpty() && !player2Name.isEmpty()) {
                startGame();
            }
            return;
        }

        // Backspace - delete character
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (isPlayer1Active && player1Name.length() > 0) {
                player1Name = player1Name.substring(0, player1Name.length() - 1);
            } else if (!isPlayer1Active && player2Name.length() > 0) {
                player2Name = player2Name.substring(0, player2Name.length() - 1);
            }
            return;
        }

        // Character input (A-Z)
        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) ('a' + (i - Input.Keys.A));
                if (isPlayer1Active && player1Name.length() < 15) {
                    player1Name += c;
                } else if (!isPlayer1Active && player2Name.length() < 15) {
                    player2Name += c;
                }
            }
        }

        // Number input (0-9)
        for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) ('0' + (i - Input.Keys.NUM_0));
                if (isPlayer1Active && player1Name.length() < 15) {
                    player1Name += c;
                } else if (!isPlayer1Active && player2Name.length() < 15) {
                    player2Name += c;
                }
            }
        }
    }

    private void startGame() {
        System.out.println("\n=== Starting Game ===");
        System.out.println("Player 1 (FireGirl): " + player1Name);
        System.out.println("Player 2 (WaterBoy): " + player2Name);
        System.out.println("Level: " + selectedLevel);

        // Store usernames in GameStateManager
        com.finpro.managers.GameStateManager.getInstance()
            .setPlayerUsernames(player1Name, player2Name);

        testingConnection = true;

        // Test connection terlebih dahulu
        ApiService apiService = ApiService.getInstance();

        apiService.testConnection(new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("✓ Backend connection OK");
                testingConnection = false;
                proceedWithGameStart();
            }

            @Override
            public void onError(String error) {
                System.err.println("✗ Backend connection failed: " + error);
                System.out.println("⚠ Starting game in offline mode");
                testingConnection = false;
                proceedWithGameStart();
            }
        });
    }

    private void proceedWithGameStart() {
        ApiService apiService = ApiService.getInstance();

        System.out.println("DEBUG: Calling startGame API...");

        apiService.startGame(
            player1Name,
            player2Name,
            selectedLevel,
            new ApiService.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    System.out.println("✓ Game session created!");

                    UUID sessionId = apiService.getCurrentSessionId();
                    System.out.println("Session ID: " + sessionId);

                    // Start actual game
                    Gdx.app.postRunnable(() -> {
                        com.finpro.managers.GameStateManager.getInstance()
                            .startLevel(selectedLevel);
                    });
                }

                @Override
                public void onError(String error) {
                    System.err.println("✗ Backend error: " + error);
                    System.out.println("⚠ Starting game anyway...");

                    // Start game even if API fails
                    Gdx.app.postRunnable(() -> {
                        com.finpro.managers.GameStateManager.getInstance()
                            .startLevel(selectedLevel);
                    });
                }
            }
        );
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
