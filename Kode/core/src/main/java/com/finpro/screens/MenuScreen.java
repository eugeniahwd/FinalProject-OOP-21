package com.finpro.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.finpro.Main;
import com.finpro.managers.GameStateManager;

public class MenuScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private BitmapFont font;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private GlyphLayout layout;
    private float timer;

    // Button properties
    private Rectangle startButton;
    private boolean isHoveringStart;

    // Constants for better proportion control
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 720;
    private static final float TITLE_SCALE = 4.0f;
    private static final float SUBTITLE_SCALE = 1.2f;
    private static final float BUTTON_TEXT_SCALE = 1.5f;
    private static final float BUTTON_WIDTH_RATIO = 0.25f; // 25% of screen width
    private static final float BUTTON_HEIGHT_RATIO = 0.1f; // 10% of screen height

    public MenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        font = new BitmapFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        layout = new GlyphLayout();
        timer = 0;

        // Calculate button size based on screen proportions
        float buttonWidth = SCREEN_WIDTH * BUTTON_WIDTH_RATIO;
        float buttonHeight = SCREEN_HEIGHT * BUTTON_HEIGHT_RATIO;

        // Initialize button (centered)
        startButton = new Rectangle(
            (SCREEN_WIDTH - buttonWidth) / 2,
            SCREEN_HEIGHT * 0.45f, // Position at 45% from top
            buttonWidth,
            buttonHeight
        );

        GameStateManager.getInstance().resetSession();
    }

    private void drawCenteredText(String text, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        layout.setText(font, text);
        float x = (SCREEN_WIDTH - layout.width) / 2;
        font.draw(batch, text, x, y);
    }

    private void drawButton() {
        // Draw button background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (isHoveringStart) {
            shapeRenderer.setColor(0.2f, 0.6f, 0.8f, 1f);
        } else {
            shapeRenderer.setColor(0.15f, 0.45f, 0.65f, 1f);
        }

        // Add rounded corners effect with multiple rectangles
        shapeRenderer.rect(startButton.x, startButton.y, startButton.width, startButton.height);

        // Add subtle inner highlight for depth
        shapeRenderer.setColor(0.25f, 0.7f, 0.9f, 0.3f);
        shapeRenderer.rect(startButton.x + 2, startButton.y + startButton.height - 5,
            startButton.width - 4, 3);

        shapeRenderer.end();

        // Draw button border with thickness
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(startButton.x, startButton.y, startButton.width, startButton.height);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    private void drawButtonText() {
        font.getData().setScale(BUTTON_TEXT_SCALE);
        font.setColor(Color.WHITE);

        // Add subtle text shadow for better readability
        font.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        layout.setText(font, "START GAME");
        float shadowOffset = 2f;
        float shadowX = startButton.x + (startButton.width - layout.width) / 2 + shadowOffset;
        float shadowY = startButton.y + (startButton.height + layout.height) / 2 - shadowOffset;
        font.draw(batch, "START GAME", shadowX, shadowY);

        // Main text
        font.setColor(Color.WHITE);
        float textX = startButton.x + (startButton.width - layout.width) / 2;
        float textY = startButton.y + (startButton.height + layout.height) / 2;
        font.draw(batch, "START GAME", textX, textY);
    }

    @Override
    public void render(float delta) {
        timer += delta;

        // Check mouse hover
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        isHoveringStart = startButton.contains(mousePos.x, mousePos.y);

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw button
        drawButton();

        batch.begin();

        // Animated title
        float pulse = (float) Math.abs(Math.sin(timer * 2)) * 0.5f + 1.5f;
        Color titleColor = new Color(1, pulse, 0, 1);

        // Calculate title and subtitle positions
        float titleY = SCREEN_HEIGHT * 0.78f; // 78% from top (lebih ke atas sedikit)
        float subtitleY = titleY - 80; // Kurangi 80px dari title (bukan persentase)

        drawCenteredText("SPARKBOUNDS", titleY, TITLE_SCALE, titleColor);

        // Subtitle - posisi lebih dekat dengan title
        drawCenteredText("FireGirl & WaterBoy Adventure", subtitleY, SUBTITLE_SCALE, Color.LIGHT_GRAY);

        // Button text
        drawButtonText();

        // Controls reminder - adjusted positions
        drawCenteredText("FireGirl: WASD  |  WaterBoy: Arrow Keys",
            SCREEN_HEIGHT * 0.28f, 1.0f, Color.GRAY);
        drawCenteredText("Press ESC to Exit",
            SCREEN_HEIGHT * 0.22f, 0.9f, Color.GRAY);

        // Credits
        drawCenteredText("Created with LibGDX",
            SCREEN_HEIGHT * 0.1f, 0.7f, new Color(0.5f, 0.5f, 0.5f, 1f));

        batch.end();

        // Handle input
        if (isHoveringStart && Gdx.input.justTouched()) {
            game.setScreen(new UsernameInputScreen(game, 1));
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
        shapeRenderer.dispose();
    }
}
