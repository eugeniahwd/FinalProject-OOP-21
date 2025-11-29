package com.finpro.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Box {
    private float x, y;
    private float velocityX, velocityY;
    private static final float SIZE = 50;
    private static final float PUSH_SPEED = 80;
    private static final float FRICTION = 0.9f;
    private static final float GRAVITY = -1100;
    private static final float MAX_FALL_SPEED = -600;

    private Rectangle bounds;
    private boolean onGround;

    public Box(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.velocityX = 0;
        this.velocityY = 0;
        this.onGround = false;
    }

    public void update(float delta) {
        // Apply gravity
        if (!onGround) {
            velocityY += GRAVITY * delta;
            if (velocityY < MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
        }

        // Apply friction
        velocityX *= FRICTION;
        if (Math.abs(velocityX) < 2) {
            velocityX = 0;
        }

        // Apply velocity
        x += velocityX * delta;
        y += velocityY * delta;

        // Update bounds
        bounds.setPosition(x, y);
    }

    public void pushLeft() {
        velocityX = -PUSH_SPEED;
    }

    public void pushRight() {
        velocityX = PUSH_SPEED;
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.valueOf("8B4513")); // Brown
        renderer.rect(x, y, SIZE, SIZE);

        // Wood texture lines
        renderer.setColor(Color.valueOf("654321")); // Darker brown
        for (int i = 1; i < 5; i++) {
            float lineY = y + (SIZE / 5) * i;
            renderer.rectLine(x + 2, lineY, x + SIZE - 2, lineY, 2);
        }

        // Vertical lines
        for (int i = 1; i < 5; i++) {
            float lineX = x + (SIZE / 5) * i;
            renderer.rectLine(lineX, y + 2, lineX, y + SIZE - 2, 2);
        }

        renderer.end();

        // Outline
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.valueOf("3E2723")); // Very dark brown
        renderer.rect(x, y, SIZE, SIZE);
        renderer.end();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        bounds.setPosition(x, y);
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        if (onGround) {
            velocityY = 0;
        }
    }

    public void setVelocityY(float vy) {
        this.velocityY = vy;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setY(float y) {
        this.y = y;
        bounds.y = y;
    }
}
