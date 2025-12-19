package com.finpro.entities;

import com.badlogic.gdx.graphics.Color;
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
            this.width = 20;
            this.height = length;
        } else {
            this.width = length;
            this.height = 20;
        }

        this.bounds = new Rectangle(x, y, width, height);
    }

    // biar yg baru dan lama compatible
    public Platform(float x, float y, float width) {
        this(x, y, width, false);
    }

    public void render(ShapeRenderer renderer) {
        // warna dasar
        renderer.setColor(Color.valueOf("8B4513")); // coklat
        renderer.rect(x, y, width, height);

        renderer.setColor(Color.valueOf("654321")); // detail

        if (isVertical) { // box vertical
            for (int i = 1; i <= 6; i++) { // detail horizontal
                float grainY = y + (i * (height / 7));
                renderer.rectLine(
                    x + 3, grainY,
                    x + width - 3, grainY,
                    1.3f
                );
            }

            renderer.setColor(Color.valueOf("5D4037"));
            for (int i = 1; i <= 2; i++) {
                float plankX = x + (i * (width / 3));
                renderer.rectLine(
                    plankX, y + 3,
                    plankX, y + height - 3,
                    1.8f
                );
            }
        } else { // box horizontal
            for (int i = 1; i <= 10; i++) { // detail vertical
                float grainX = x + (i * (width / 11));
                renderer.rectLine(
                    grainX, y + 3,
                    grainX, y + height - 3,
                    1.3f
                );
            }

            renderer.setColor(Color.valueOf("5D4037"));
            for (int i = 1; i <= 3; i++) {
                float plankY = y + (i * (height / 4));
                renderer.rectLine(
                    x + 3, plankY,
                    x + width - 3, plankY,
                    1.8f
                );
            }
        }

        // details lagi
        renderer.setColor(Color.valueOf("3E2723"));
        if (isVertical) {
            renderer.circle(x + width/2, y + height/4, 2f);
            renderer.circle(x + width/3, y + height/2, 1.8f);
            renderer.circle(x + 2*width/3, y + 3*height/4, 2.2f);
        } else {
            renderer.circle(x + width/4, y + height/2, 2f);
            renderer.circle(x + width/2, y + height/2, 2.5f);
            renderer.circle(x + 3*width/4, y + height/2, 1.8f);
        }

        // border
        renderer.setColor(Color.valueOf("3E2723"));
        renderer.rect(x, y, width, 1);
        renderer.rect(x, y + height - 1, width, 1);
        renderer.rect(x, y, 1, height);
        renderer.rect(x + width - 1, y, 1, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isVertical() { return isVertical; }
}
