package com.finpro.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Key Entity - Required to unlock the door
 */
public class Key {
    private float x, y;
    private static final float SIZE = 25;
    private boolean collected;
    private Rectangle bounds;
    private Texture texture;
    private float bobTimer;  // For floating animation
    private boolean useTexture;

    public Key(float x, float y) {
        this.x = x;
        this.y = y;
        this.collected = false;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.bobTimer = 0;

        // Try to load texture, fallback to shape rendering
        try {
            this.texture = new Texture("key.png");
            this.useTexture = true;
            System.out.println("Key texture loaded successfully");
        } catch (Exception e) {
            System.out.println("Key texture not found, using shape renderer");
            this.useTexture = false;
        }
    }

    public void update(float delta) {
        if (!collected) {
            // Floating animation
            bobTimer += delta * 3;
        }
    }

    public void render(SpriteBatch batch) {
        if (!collected && useTexture) {
            float bobOffset = (float) Math.sin(bobTimer) * 5;
            batch.draw(texture, x, y + bobOffset, SIZE, SIZE);
        }
    }

    public void renderShape(ShapeRenderer renderer) {
        if (!collected && !useTexture) {
            float bobOffset = (float) Math.sin(bobTimer) * 5;

            // Draw key shape
            renderer.setColor(Color.GOLD);
            // Key head (circle)
            renderer.circle(x + SIZE/2, y + bobOffset + SIZE * 0.7f, SIZE * 0.3f);
            // Key shaft (rectangle)
            renderer.rect(x + SIZE/2 - 2, y + bobOffset, 4, SIZE * 0.5f);
            // Key teeth
            renderer.rect(x + SIZE/2 - 6, y + bobOffset, 4, 6);
            renderer.rect(x + SIZE/2 + 2, y + bobOffset, 4, 4);
        }
    }

    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
