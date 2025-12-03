package com.finpro.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Platform {
    private float x, y, width, height;
    private boolean isVertical;
    private Rectangle bounds;

    public Platform(float x, float y, float length, boolean isVertical) {
        this.x = x;
        this.y = y;
        this.isVertical = isVertical;

        if (isVertical) {
            this.width = 20; // Ketebalan platform vertikal
            this.height = length; // Panjang platform vertikal
        } else {
            this.width = length; // Panjang platform horizontal
            this.height = 20; // Ketebalan platform horizontal
        }

        this.bounds = new Rectangle(x, y, width, height);
    }

    // Constructor overload untuk backward compatibility
    public Platform(float x, float y, float width) {
        this(x, y, width, false); // Default horizontal
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.valueOf("8B4513"));
        renderer.rect(x, y, width, height);

        // Add outline for better visibility
        renderer.end();
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.valueOf("654321"));
        renderer.rect(x, y, width, height);
        renderer.end();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    // Getter methods
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isVertical() { return isVertical; }
}
