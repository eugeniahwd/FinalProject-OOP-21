package com.finpro.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle {
    public enum ObstacleType { FIRE, WATER }

    private Rectangle visualBounds;
    private Rectangle collisionBounds;
    private Texture texture;
    private ObstacleType type;
    private float visualScale;

    public Obstacle(float x, float y, ObstacleType type) {
        this.type = type;

        String texturePath = (type == ObstacleType.FIRE) ?
            "lava.png" : "water.png";
        this.texture = new Texture(texturePath);

        this.visualScale = 0.1f;
        float visualWidth = texture.getWidth() * visualScale;
        float visualHeight = texture.getHeight() * visualScale;

        this.visualBounds = new Rectangle(x, y, visualWidth, visualHeight);


        float collisionWidth = 60f;
        float collisionHeight = 30f;

        // collision di tengah visual
        float collisionX = x + (visualWidth - collisionWidth) / 2;
        float collisionY = y + (visualHeight - collisionHeight) / 2;

        this.collisionBounds = new Rectangle(collisionX, collisionY,
            collisionWidth, collisionHeight);

    }

    public void render(SpriteBatch batch) {
        // Render pake skala visual
        batch.draw(texture,
            visualBounds.x, visualBounds.y,
            visualBounds.width, visualBounds.height);
    }

    // collision detection
    public Rectangle getBounds() {
        return collisionBounds;
    }

    // debug rendering
    public Rectangle getVisualBounds() {
        return visualBounds;
    }

    public ObstacleType getType() {
        return type;
    }

    public Texture getTexture() {
        return texture;
    }

    public void dispose() {
        texture.dispose();
    }
}
