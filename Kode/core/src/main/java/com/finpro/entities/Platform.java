package com.finpro.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Platform {
    private float x, y, width;
    private static final float HEIGHT = 20;
    private Rectangle bounds;

    public Platform(float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.bounds = new Rectangle(x, y, width, HEIGHT);
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.valueOf("8B4513"));
        renderer.rect(x, y, width, HEIGHT);

        // Add outline for better visibility
        renderer.end();
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.valueOf("654321"));
        renderer.rect(x, y, width, HEIGHT);
        renderer.end();
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
