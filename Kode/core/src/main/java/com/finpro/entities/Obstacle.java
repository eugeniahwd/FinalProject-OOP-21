package com.finpro.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle {
    public enum ObstacleType {FIRE, WATER}

    private float x, y, width, height;
    private ObstacleType type;
    private Rectangle bounds;
    private Texture texture;

    public Obstacle(float x, float y, ObstacleType type) {
        this.x = x;
        this.y = y;
        this.width = 200;
        this.height = 75;
        this.type = type;
        this.bounds = new Rectangle(x, y, width, height);

        if (type == ObstacleType.FIRE) {
            texture = new Texture("lava.png");
        } else {
            texture = new Texture("water.png");
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public ObstacleType getType() {
        return type;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
