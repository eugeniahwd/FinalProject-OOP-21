package com.finpro.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.finpro.Main;
import com.finpro.states.*;

/**
 * Player class menggunakan STATE PATTERN - IMPROVED PHYSICS
 */
public class Player {
    public enum PlayerType { FIREGIRL, WATERBOY }

    private float x, y;
    private float velocityX, velocityY;
    private static final float WIDTH = 45;
    private static final float HEIGHT = 55;
    private static final float SPEED = 220;
    private static final float JUMP_FORCE = 450;
    private static final float DOUBLE_JUMP_FORCE = 470;
    private static final float GRAVITY = -1100;
    private static final float MAX_FALL_SPEED = -600;
    private static final float FRICTION = 0.70f;

    private PlayerType type;
    private Rectangle bounds;
    private Texture texture;
    private boolean isOnGround;
    private boolean isDead;
    private Box standingOnBox = null;

    // Double jump mechanic
    private boolean hasDoubleJump;
    private int jumpCount;
    private static final int MAX_JUMPS = 2;

    // Coyote time
    private float coyoteTime = 0;
    private static final float COYOTE_TIME_MAX = 0.1f;

    // STATE PATTERN
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
        this.jumpCount = 0;
        this.hasDoubleJump = true;

        if (type == PlayerType.FIREGIRL) {
            texture = new Texture("fireGirl.png");
            System.out.println("FireGirl created at: " + x + ", " + y);
        } else {
            texture = new Texture("waterBoy.png");
            System.out.println("WaterBoy created at: " + x + ", " + y);
        }
    }

    public void setState(PlayerState newState) {
        this.currentState = newState;
    }

    public void update(float delta) {
        if (isDead) return;

        currentState.update(this, delta);

        if (isOnGround) {
            coyoteTime = COYOTE_TIME_MAX;
        } else if (coyoteTime > 0) {
            coyoteTime -= delta;
        }

        if (!isOnGround) {
            velocityY += GRAVITY * delta;
            if (velocityY < MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
        }

        y += velocityY * delta;
        x += velocityX * delta;

        // FIXED: Gunakan WORLD_WIDTH bukan 800
        if (x < 0) x = 0;
        if (x > Main.WORLD_WIDTH - WIDTH) x = Main.WORLD_WIDTH - WIDTH;

        if (y < -100) {
            die();
        }

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

        if (jumpCount == 0 && (isOnGround || coyoteTime > 0)) {
            velocityY = JUMP_FORCE;
            isOnGround = false;
            coyoteTime = 0;
            jumpCount = 1;
            setState(new JumpingState());
            System.out.println(type + " - Single Jump!");
        }
        else if (jumpCount == 1 && hasDoubleJump) {
            velocityY = DOUBLE_JUMP_FORCE;
            jumpCount = 2;
            setState(new JumpingState());
            System.out.println(type + " - DOUBLE Jump!");
        }
    }

    public void setOnGround(boolean onGround) {
        boolean wasInAir = !this.isOnGround;
        this.isOnGround = onGround;

        if (onGround) {
            jumpCount = 0;
            hasDoubleJump = true;

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
        if (isDead) return;
        isDead = true;
        velocityX = 0;
        velocityY = 0;
        setState(new DeadState());

        String playerName = (type == PlayerType.FIREGIRL) ? "FireGirl" : "WaterBoy";
        System.out.println(playerName + " died at position: " + x + ", " + y);
    }

    public void setStandingOnBox(Box box) {
        this.standingOnBox = box;
    }

    public Box getStandingOnBox() {
        return standingOnBox;
    }

    public void render(SpriteBatch batch) {
        // FIXED: Selalu reset color
        if (isDead) {
            batch.setColor(1, 1, 1, 0.5f);
        } else {
            batch.setColor(1, 1, 1, 1);
        }

        batch.draw(texture, x, y, WIDTH, HEIGHT);

        // CRITICAL: Reset color setelah draw
        batch.setColor(1, 1, 1, 1);
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
    public void setVelocityX(float vx) {this.velocityX = vx;}
    public boolean isOnGround() { return isOnGround; }
    public boolean isDead() { return isDead; }
    public int getJumpCount() { return jumpCount; }
    public boolean hasDoubleJump() { return hasDoubleJump; }

    public void dispose() {
        texture.dispose();
    }
}
