package com.finpro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.finpro.Main;
import com.finpro.facade.GameFacade;
import com.finpro.service.ApiService;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class VictoryScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout layout;
    private float timer;
    private GameFacade gameFacade;
    private boolean dataSaved = false;
    private boolean savingData = false;

    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;
    private Texture whiteBoxTexture;

    private String player1Username;
    private String player2Username;

    public VictoryScreen(Main game, GameFacade gameFacade, String player1Username, String player2Username) {
        this.game = game;
        this.gameFacade = gameFacade;
        this.player1Username = player1Username;
        this.player2Username = player2Username;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        // bg
        backgroundTexture = new Texture(Gdx.files.internal("walls.png"));
        backgroundRegion = new TextureRegion(backgroundTexture);

        createWhiteBoxTexture();

        font = new BitmapFont();
        titleFont = new BitmapFont();

        font.getData().setScale(1.1f);
        font.setColor(Color.WHITE);

        titleFont.getData().setScale(3f);
        titleFont.setColor(new Color(1f, 0.8f, 0.2f, 1)); // Gold color

        layout = new GlyphLayout();
        timer = 0;

        saveGameResults();
    }

    private void createWhiteBoxTexture() {
        Pixmap pixmap = new Pixmap(600, 465, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1f, 1f, 1f, 0.85f));
        pixmap.fillRectangle(0, 0, 600, 465);
        whiteBoxTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void drawCenteredText(BitmapFont font, String text, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        layout.setText(font, text);
        float x = (1280 - layout.width) / 2;
        font.draw(game.batch, text, x, y);
    }

    private void drawCenteredText(String text, float y, float scale, Color color) {
        drawCenteredText(font, text, y, scale, color);
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
        // clear color
        Gdx.gl.glClearColor(0.95f, 0.95f, 0.95f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        // draw bg
        game.batch.draw(backgroundRegion, 0, 0, 1280, 720);
        // efek
        float pulse = (float) Math.sin(timer * 3) * 0.15f + 0.85f;

        Color victoryColor = new Color(1f, 0.85f, 0.1f, pulse);
        drawCenteredText(titleFont, "VICTORY!", 650, 3.5f, victoryColor);

        float boxX = (1280 - 600) / 2;
        float boxY = 100;
        game.batch.setColor(1, 1, 1, 0.25f);
        game.batch.draw(whiteBoxTexture, boxX, boxY);
        game.batch.setColor(Color.WHITE);

        Color darkColor = new Color(0.2f, 0.2f, 0.2f, 1);

        drawCenteredText(font, "Level Complete!", 560, 1.8f, darkColor);

        drawCenteredText(font, "GAME STATISTICS", 520, 1.6f, new Color(0.9f, 0.4f, 0.1f, 1));

        drawCenteredText(font, player1Username != null ? player1Username : "FireGirl",
            485, 1.3f, new Color(0.8f, 0.2f, 0.2f, 1));
        drawCenteredText(font, player2Username != null ? player2Username : "WaterBoy",
            455, 1.3f, new Color(0.2f, 0.4f, 0.8f, 1));

        // stats firegirl
        drawCenteredText(font, "Red Diamonds: " + gameFacade.getFireGirlRedDiamonds(),
            420, 1.2f, new Color(0.7f, 0.1f, 0.1f, 1));
        drawCenteredText(font, "Score: " + gameFacade.getFireGirlScore(),
            390, 1.2f, new Color(0.8f, 0.3f, 0.3f, 1));

        // stats waterboy
        drawCenteredText(font, "Blue Diamonds: " + gameFacade.getWaterBoyBlueDiamonds(),
            355, 1.2f, new Color(0.1f, 0.3f, 0.7f, 1));
        drawCenteredText(font, "Score: " + gameFacade.getWaterBoyScore(),
            325, 1.2f, new Color(0.2f, 0.5f, 0.8f, 1));

        game.batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1));
        game.batch.draw(whiteBoxTexture, boxX + 50, 305, 500, 2);
        game.batch.setColor(Color.WHITE);
        // score total
        int totalScore = gameFacade.getFireGirlScore() + gameFacade.getWaterBoyScore();
        drawCenteredText(font, "TOTAL SCORE: " + totalScore, 285, 1.5f,
            new Color(0.9f, 0.7f, 0.1f, 1)); // Gold solid
        // time
        drawCenteredText(font, "Time: " + gameFacade.getGameTimeSeconds() + " seconds",
            250, 1.2f, darkColor);
        // informasi key
        drawCenteredText(font, "Keys: " + gameFacade.getKeysCollected() + "/" + gameFacade.getTotalKeys(),
            220, 1.1f, darkColor);

        int currentLevel = com.finpro.managers.GameStateManager.getInstance().getCurrentLevel();
        Color actionColor = new Color(0.2f, 0.5f, 0.8f, 1);

        if (currentLevel < 3) {
            drawCenteredText(font, "Press ENTER for Next Level",
                150, 1.3f, actionColor);
        } else {
            drawCenteredText(font, "Press ENTER to Return to Main Menu",
                150, 1.3f, actionColor);
        }

        drawCenteredText(font, "Press M for Main Menu  |  Press ESC to Exit",
            120, 1f, darkColor);

        game.batch.end();

        // input
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

        // Reset font scale
        font.getData().setScale(1.1f);
        titleFont.getData().setScale(3f);
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
        titleFont.dispose();
        backgroundTexture.dispose();
        whiteBoxTexture.dispose();
    }
}
