package com.finpro.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.finpro.states.*;


public class Player {
    public enum PlayerType { FIREGIRL, WATERBOY }

    private float x, y;
    private float velocityX, velocityY;
    private static final float WIDTH = 40;
    private static final float HEIGHT = 50;
    private static final float SPEED = 220;
    private static final float JUMP_FORCE = 500;
    private static final float GRAVITY = -1100;
    private static final float MAX_FALL_SPEED = -600;
    private static final float FRICTION = 0.85f;

    private PlayerType type;
    private Rectangle bounds;
    private Texture texture;
    private boolean isOnGround;
    private boolean isDead;

    private float coyoteTime = 0;
    private static final float COYOTE_TIME_MAX = 0.1f;

    private PlayerState currentState;

    public Player(float x, float y, PlayerType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        this.currentState = new IdleState();
        this.isOnGround = true;
        this.isDead = false;
        this.velocityX = 0;
        this.velocityY = 0;
        this.coyoteTime = COYOTE_TIME_MAX;

        if (type == PlayerType.FIREGIRL) {
            texture = new Texture("fireBoy.png");
            System.out.println("FireBoy created at: " + x + ", " + y);
        } else {
            texture = new Texture("waterGirl.png");
            System.out.println("WaterGirl created at: " + x + ", " + y);
        }
    }

    public void setState(PlayerState newState) {
        this.currentState = newState;
    }

    public void update(float delta) {
        if (isDead) return;

        // Update state
        currentState.update(this, delta);

        // Update coyote time
        if (isOnGround) {
            coyoteTime = COYOTE_TIME_MAX;
        } else if (coyoteTime > 0) {
            coyoteTime -= delta;
        }

        if (!isOnGround) {
            velocityY += GRAVITY * delta;

            // Limit fall speed
            if (velocityY < MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
        }

        // Apply velocity
        y += velocityY * delta;
        x += velocityX * delta;

        // Keep player in bounds
        if (x < 0) x = 0;
        if (x > 800 - WIDTH) x = 800 - WIDTH;

        // Death if fall off screen
        if (y < -100) {
            die();
        }

        // Update bounds
        bounds.setPosition(x, y);
    }

    public void moveLeft() {
        if (isDead) return;
        velocityX = -SPEED;
        if (isOnGround) {
            setState(new MovingState());
        }
    }

    public void moveRight() {
        if (isDead) return;
        velocityX = SPEED;
        if (isOnGround) {
            setState(new MovingState());
        }
    }

    public void stopMoving() {
        velocityX *= FRICTION;
        if (Math.abs(velocityX) < 5) {
            velocityX = 0;
        }

        if (isOnGround && !isDead && velocityX == 0) {
            setState(new IdleState());
        }
    }

    public void jump() {
        if (isDead) return;
        if (isOnGround || coyoteTime > 0) {
            velocityY = JUMP_FORCE;
            isOnGround = false;
            coyoteTime = 0;
            setState(new JumpingState());
        }
    }

    public void setOnGround(boolean onGround) {
        boolean wasInAir = !this.isOnGround;
        this.isOnGround = onGround;

        if (onGround) {
            if (wasInAir && velocityY < 0) {
                velocityY = 0;
            }

            if (velocityX == 0 && !isDead) {
                setState(new IdleState());
            } else if (!isDead) {
                setState(new MovingState());
            }
        }
    }

    public void die() {
        if (isDead) return; // Prevent double death
        isDead = true;
        velocityX = 0;
        velocityY = 0;
        setState(new DeadState());

        String playerName = (type == PlayerType.FIREGIRL) ? "FireGirl" : "WaterBoy";
        System.out.println(playerName + " died at position: " + x + ", " + y);
    }

    public void render(SpriteBatch batch) {
        // Add slight transparency if dead
        if (isDead) {
            batch.setColor(1, 1, 1, 0.5f);
        }
        batch.draw(texture, x, y, WIDTH, HEIGHT);
        if (isDead) {
            batch.setColor(1, 1, 1, 1); // Reset color
        }
    }

    // Getters and Setters
    public Rectangle getBounds() { return bounds; }
    public PlayerType getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
    public void setY(float y) {
        this.y = y;
        bounds.y = y;
    }
    public void setX(float x) {
        this.x = x;
        bounds.x = x;
    }
    public float getVelocityY() { return velocityY; }
    public void setVelocityY(float vy) { this.velocityY = vy; }
    public boolean isOnGround() { return isOnGround; }
    public boolean isDead() { return isDead; }

    public void dispose() {
        texture.dispose();
    }
}
