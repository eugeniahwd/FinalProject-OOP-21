package com.finpro.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle {
    public enum ObstacleType { FIRE, WATER }

    private Rectangle visualBounds;    // Untuk rendering (1090x1080, tapi discale)
    private Rectangle collisionBounds; // Untuk collision (lebih kecil)
    private Texture texture;
    private ObstacleType type;
    private float visualScale;         // Skala untuk visual

    public Obstacle(float x, float y, ObstacleType type) {
        this.type = type;

        String texturePath = (type == ObstacleType.FIRE) ?
            "lava.png" : "water.png";
        this.texture = new Texture(texturePath);

        // 1. VISUAL BOUNDS (untuk rendering)
        // Skala kecil untuk visual, tapi posisi Y tetap sesuai yang diinginkan
        this.visualScale = 0.1f; // 10% dari asli (coba 0.1f dulu)
        float visualWidth = texture.getWidth() * visualScale;
        float visualHeight = texture.getHeight() * visualScale;

        // Posisi visual: tetap di (x, y) seperti yang sudah diatur
        this.visualBounds = new Rectangle(x, y, visualWidth, visualHeight);

        // 2. COLLISION BOUNDS (untuk deteksi tabrakan)
        // Lebih kecil dari visual, dan bisa diatur offset-nya
        float collisionWidth = 60f;    // Lebar collision
        float collisionHeight = 30f;   // Tinggi collision

        // Pusatkan collision di tengah visual
        float collisionX = x + (visualWidth - collisionWidth) / 2;
        float collisionY = y + (visualHeight - collisionHeight) / 2;

        this.collisionBounds = new Rectangle(collisionX, collisionY,
            collisionWidth, collisionHeight);

    }

    public void render(SpriteBatch batch) {
        // Render dengan skala visual
        batch.draw(texture,
            visualBounds.x, visualBounds.y,
            visualBounds.width, visualBounds.height);
    }

    // Untuk collision detection
    public Rectangle getBounds() {
        return collisionBounds;
    }

    // Untuk debug rendering
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
