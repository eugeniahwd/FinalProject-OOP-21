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

    // properti moving box
    private float startX, endX;
    private float startY, endY;
    private float moveSpeed = 100;
    private boolean movingRight = true;
    private boolean movingUp = true;
    private boolean isMovingPlatform = false;
    private boolean isVertical = false;

    private Rectangle bounds;
    private boolean onGround;

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

    // constructor moving box horizontal
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

    // constructor moving box vertical
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
            this.moveSpeed = 80;
            this.movingUp = true;
        }
    }

    public void update(float delta) {
        // gerak pake velocity
        if (isMovingPlatform) {
            if (isVertical) {
                if (movingUp) {
                    velocityY = moveSpeed; // vertical
                    if (y >= endY) {
                        y = endY;
                        movingUp = false;
                    }
                } else {
                    velocityY = -moveSpeed;
                    if (y <= startY) {
                        y = startY;
                        movingUp = true;
                    }
                }
                velocityX = 0;
            } else {
                if (movingRight) {
                    velocityX = moveSpeed; // horizontal
                    if (x >= endX) {
                        x = endX;
                        movingRight = false;
                    }
                } else {
                    velocityX = -moveSpeed;
                    if (x <= startX) {
                        x = startX;
                        movingRight = true;
                    }
                }
                velocityY = 0;
            }

            x += velocityX * delta;
            y += velocityY * delta;
        }

        bounds.setPosition(x, y);
    }

    public void render(ShapeRenderer renderer) {
        if (isMovingPlatform) {
            if (isVertical) {
                renderer.setColor(Color.valueOf("9C27B0")); // ungu
            } else {
                renderer.setColor(Color.valueOf("FF9800")); // orange
            }
        } else {
            renderer.setColor(Color.valueOf("8B4513"));
        }
        renderer.rect(x, y, SIZE, SIZE);

        // texture wood
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

        // arrow arah
        if (isMovingPlatform) {
            renderer.setColor(Color.WHITE);

            if (isVertical) { // vertical
                float arrowX = x + SIZE/2;
                if (movingUp) {
                    // ^
                    renderer.triangle(
                        arrowX, y + SIZE - 10,
                        arrowX - 5, y + SIZE - 15,
                        arrowX + 5, y + SIZE - 15
                    );
                } else {
                    // v
                    renderer.triangle(
                        arrowX, y + 10,
                        arrowX - 5, y + 15,
                        arrowX + 5, y + 15
                    );
                }
            } else { // horizontal
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
                renderer.setColor(Color.valueOf("4A148C"));
            } else {
                renderer.setColor(Color.valueOf("E65100"));
            }
        } else {
            renderer.setColor(Color.valueOf("3E2723"));
        }
        renderer.rect(x, y, SIZE, SIZE);
    }

    public Rectangle getBounds() { return bounds; }
    public float getVelocityY() { return velocityY; }
    public float getVelocityX() { return velocityX; }
    public float getX() { return x; }
    public float getY() { return y; }
    public boolean isMovingPlatform() { return isMovingPlatform; }
    public boolean isVertical() { return isVertical; }

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
