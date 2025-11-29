package com.finpro.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Door {
    private float x, y;
    private static final float WIDTH = 60;
    private static final float HEIGHT = 90;
    private Rectangle bounds;
    private boolean locked;

    public Door(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        this.locked = true;
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        if (locked) {
            renderer.setColor(Color.valueOf("5D4037"));
        } else {
            renderer.setColor(Color.valueOf("8D6E63"));
        }
        renderer.rect(x, y, WIDTH, HEIGHT);

        // Wood texture lines
        renderer.setColor(Color.valueOf("4E342E"));
        for (int i = 1; i < 6; i++) {
            float lineY = y + (HEIGHT / 6) * i;
            renderer.rectLine(x + 5, lineY, x + WIDTH - 5, lineY, 2);
        }

        // Vertical center line
        renderer.rectLine(x + WIDTH/2, y + 5, x + WIDTH/2, y + HEIGHT - 5, 3);

        renderer.end();

        // Door frame
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.valueOf("3E2723")); // Very dark brown
        renderer.rect(x, y, WIDTH, HEIGHT);
        renderer.end();

        // Door handle
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GOLD);
        float handleX = x + WIDTH - 12;
        float handleY = y + HEIGHT / 2;
        renderer.circle(handleX, handleY, 4);
        renderer.end();

        // Lock indicator
        if (locked) {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(Color.RED);
            renderer.circle(x + WIDTH/2, y + HEIGHT - 15, 6);

            // Keyhole
            renderer.setColor(Color.valueOf("2C1810"));
            renderer.circle(x + WIDTH/2, y + HEIGHT - 15, 3);
            renderer.end();
        } else {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(Color.GREEN);
            renderer.circle(x + WIDTH/2, y + HEIGHT - 15, 6);
            renderer.end();
        }
    }

    public void unlock() {
        locked = false;
    }

    public boolean isLocked() {
        return locked;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
