package com.finpro.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Diamond {
    public enum DiamondType { RED, BLUE }

    private float x, y;
    private static final float SIZE = 30;
    private DiamondType type;
    private boolean collected;
    private Rectangle bounds;
    private Texture texture;

    public Diamond(float x, float y, DiamondType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.collected = false;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        loadTexture(type);
    }

    private void loadTexture(DiamondType type) {
        if (texture != null) {
            texture.dispose();
        }

        if (type == DiamondType.RED) {
            texture = new Texture("redDiamond.png");
        } else {
            texture = new Texture("blueDiamond.png");
        }
    }

    public void reset(float x, float y, DiamondType type) {
        this.x = x;
        this.y = y;
        this.collected = false;
        bounds.setPosition(x, y);

        if (this.type != type) {
            this.type = type;
            loadTexture(type);
        }
    }

    public void render(SpriteBatch batch) {
        if (!collected) {
            batch.draw(texture, x, y, SIZE, SIZE);
        }
    }

    public void collect() { collected = true; }
    public boolean isCollected() { return collected; }
    public DiamondType getType() { return type; }
    public Rectangle getBounds() { return bounds; }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
