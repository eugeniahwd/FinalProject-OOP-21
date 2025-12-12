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

    // Constructor overload untuk backward compatibility
    public Platform(float x, float y, float width) {
        this(x, y, width, false); // Default horizontal
    }

    // INI SATU-SATUNYA METHOD YANG DIPANGGIL oleh GameFacade
    public void render(ShapeRenderer renderer) {
        // Base wood color (dark brown)
        renderer.setColor(Color.valueOf("8B4513")); // Saddle Brown - base wood
        renderer.rect(x, y, width, height);

        // Wood grain pattern (slightly darker lines)
        renderer.setColor(Color.valueOf("654321")); // Dark brown for grains

        if (isVertical) {
            // Vertical platform - horizontal wood grains
            // 5-6 horizontal lines untuk wood grain effect
            for (int i = 1; i <= 6; i++) {
                float grainY = y + (i * (height / 7));
                renderer.rectLine(
                    x + 3, grainY,          // Start point
                    x + width - 3, grainY,  // End point
                    1.3f                     // Line thickness
                );
            }

            // Vertical plank divisions (makes it look like planks)
            renderer.setColor(Color.valueOf("5D4037")); // Darker for plank edges
            for (int i = 1; i <= 2; i++) {
                float plankX = x + (i * (width / 3));
                renderer.rectLine(
                    plankX, y + 3,
                    plankX, y + height - 3,
                    1.8f
                );
            }
        } else {
            // Horizontal platform - vertical wood grains
            // 10-12 vertical lines untuk wood grain effect
            for (int i = 1; i <= 10; i++) {
                float grainX = x + (i * (width / 11));
                renderer.rectLine(
                    grainX, y + 3,          // Start point
                    grainX, y + height - 3, // End point
                    1.3f                     // Line thickness
                );
            }

            // Horizontal plank divisions
            renderer.setColor(Color.valueOf("5D4037")); // Darker for plank edges
            for (int i = 1; i <= 3; i++) {
                float plankY = y + (i * (height / 4));
                renderer.rectLine(
                    x + 3, plankY,
                    x + width - 3, plankY,
                    1.8f
                );
            }
        }

        // Wood knots (circles that look like wood knots)
        renderer.setColor(Color.valueOf("3E2723")); // Very dark brown
        if (isVertical) {
            // 3 knots untuk platform vertikal
            renderer.circle(x + width/2, y + height/4, 2f);
            renderer.circle(x + width/3, y + height/2, 1.8f);
            renderer.circle(x + 2*width/3, y + 3*height/4, 2.2f);
        } else {
            // 3 knots untuk platform horizontal
            renderer.circle(x + width/4, y + height/2, 2f);
            renderer.circle(x + width/2, y + height/2, 2.5f);
            renderer.circle(x + 3*width/4, y + height/2, 1.8f);
        }

        // Simple border/edge (optional)
        renderer.setColor(Color.valueOf("3E2723"));
        renderer.rect(x, y, width, 1); // Top edge
        renderer.rect(x, y + height - 1, width, 1); // Bottom edge
        renderer.rect(x, y, 1, height); // Left edge
        renderer.rect(x + width - 1, y, 1, height); // Right edge
    }

    public Rectangle getBounds() {
        return bounds;
    }

    // Getter methods (tetap sama)
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isVertical() { return isVertical; }
}
