package com.finpro.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Box {
    private float x, y;
    private float velocityX, velocityY;
    private static final float SIZE = 50;
    private static final float GRAVITY = -1100;
    private static final float MAX_FALL_SPEED = -600;

    // Moving platform properties
    private float startX, endX;  // Untuk horizontal
    private float startY, endY;  // Untuk vertical
    private float moveSpeed = 100;
    private boolean movingRight = true;
    private boolean movingUp = true;  // Untuk vertical
    private boolean isMovingPlatform = false;
    private boolean isVertical = false;  // NEW: Flag untuk vertical movement

    private Rectangle bounds;
    private boolean onGround;

    // Constructor untuk regular box (tidak terpakai lagi)
    public Box(float x, float y) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.velocityX = 0;
        this.velocityY = 0;
        this.onGround = false;
        this.isMovingPlatform = false;
        this.isVertical = false;
    }

    // Constructor untuk HORIZONTAL moving platform
    public Box(float x, float y, float startX, float endX) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.velocityX = 0;
        this.velocityY = 0;
        this.onGround = true;

        this.isMovingPlatform = true;
        this.isVertical = false;
        this.startX = startX;
        this.endX = endX;
        this.moveSpeed = 100;
        this.movingRight = true;
    }

    // Constructor untuk VERTICAL moving platform (NEW!)
    public Box(float x, float y, float startY, float endY, boolean isVertical) {
        this.x = x;
        this.y = y;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        this.velocityX = 0;
        this.velocityY = 0;
        this.onGround = true;

        this.isMovingPlatform = true;
        this.isVertical = isVertical;

        if (isVertical) {
            this.startY = startY;
            this.endY = endY;
            this.moveSpeed = 80;  // Bisa lebih lambat untuk vertical
            this.movingUp = true;
        }
    }

    public void update(float delta) {
        if (isMovingPlatform) {
            if (isVertical) {
                // VERTICAL MOVEMENT
                if (movingUp) {
                    y += moveSpeed * delta;
                    if (y >= endY) {
                        y = endY;
                        movingUp = false;
                    }
                } else {
                    y -= moveSpeed * delta;
                    if (y <= startY) {
                        y = startY;
                        movingUp = true;
                    }
                }

                velocityX = 0;
                velocityY = movingUp ? moveSpeed : -moveSpeed;
            } else {
                // HORIZONTAL MOVEMENT (existing code)
                if (movingRight) {
                    x += moveSpeed * delta;
                    if (x >= endX) {
                        x = endX;
                        movingRight = false;
                    }
                } else {
                    x -= moveSpeed * delta;
                    if (x <= startX) {
                        x = startX;
                        movingRight = true;
                    }
                }

                velocityX = movingRight ? moveSpeed : -moveSpeed;
                velocityY = 0;
            }
        } else {
            // Regular box with gravity (not used)
            if (!onGround) {
                velocityY += GRAVITY * delta;
                if (velocityY < MAX_FALL_SPEED) {
                    velocityY = MAX_FALL_SPEED;
                }
            }

            x += velocityX * delta;
            y += velocityY * delta;
        }

        bounds.setPosition(x, y);
    }

    public void render(ShapeRenderer renderer) {
        // Color berbeda untuk vertical moving platform
        if (isMovingPlatform) {
            if (isVertical) {
                renderer.setColor(Color.valueOf("9C27B0")); // Purple untuk vertical
            } else {
                renderer.setColor(Color.valueOf("FF9800")); // Orange untuk horizontal
            }
        } else {
            renderer.setColor(Color.valueOf("8B4513"));
        }
        renderer.rect(x, y, SIZE, SIZE);

        // Wood texture
        if (isMovingPlatform) {
            if (isVertical) {
                renderer.setColor(Color.valueOf("7B1FA2"));
            } else {
                renderer.setColor(Color.valueOf("F57C00"));
            }
        } else {
            renderer.setColor(Color.valueOf("654321"));
        }

        for (int i = 1; i < 5; i++) {
            float lineY = y + (SIZE / 5) * i;
            renderer.rectLine(x + 2, lineY, x + SIZE - 2, lineY, 2);
        }

        for (int i = 1; i < 5; i++) {
            float lineX = x + (SIZE / 5) * i;
            renderer.rectLine(lineX, y + 2, lineX, y + SIZE - 2, 2);
        }

        // Direction arrow
        if (isMovingPlatform) {
            renderer.setColor(Color.WHITE);

            if (isVertical) {
                // VERTICAL ARROW
                float arrowX = x + SIZE/2;
                if (movingUp) {
                    // Arrow pointing UP
                    renderer.triangle(
                        arrowX, y + SIZE - 10,
                        arrowX - 5, y + SIZE - 15,
                        arrowX + 5, y + SIZE - 15
                    );
                } else {
                    // Arrow pointing DOWN
                    renderer.triangle(
                        arrowX, y + 10,
                        arrowX - 5, y + 15,
                        arrowX + 5, y + 15
                    );
                }
            } else {
                // HORIZONTAL ARROW (existing)
                float arrowY = y + SIZE/2;
                if (movingRight) {
                    renderer.triangle(
                        x + SIZE - 15, arrowY,
                        x + SIZE - 10, arrowY - 5,
                        x + SIZE - 10, arrowY + 5
                    );
                } else {
                    renderer.triangle(
                        x + 15, arrowY,
                        x + 10, arrowY - 5,
                        x + 10, arrowY + 5
                    );
                }
            }
        }
    }

    public void renderOutline(ShapeRenderer renderer) {
        if (isMovingPlatform) {
            if (isVertical) {
                renderer.setColor(Color.valueOf("4A148C")); // Dark purple
            } else {
                renderer.setColor(Color.valueOf("E65100"));
            }
        } else {
            renderer.setColor(Color.valueOf("3E2723"));
        }
        renderer.rect(x, y, SIZE, SIZE);
    }

    // Getters
    public Rectangle getBounds() { return bounds; }
    public float getVelocityY() { return velocityY; }
    public float getVelocityX() { return velocityX; }
    public float getX() { return x; }
    public float getY() { return y; }
    public boolean isMovingPlatform() { return isMovingPlatform; }
    public boolean isVertical() { return isVertical; }

    // Setters
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        bounds.setPosition(x, y);
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
        if (onGround && !isMovingPlatform) {
            velocityY = 0;
        }
    }

    public void setVelocityY(float vy) {
        if (!isMovingPlatform) {
            this.velocityY = vy;
        }
    }

    public void setY(float y) {
        this.y = y;
        bounds.y = y;
    }

    public void setX(float newX) {
        this.x = newX;
        bounds.x = newX;
    }
}
