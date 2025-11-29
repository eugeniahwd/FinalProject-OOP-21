package com.finpro.facade;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.finpro.Main;
import com.finpro.entities.*;
import com.finpro.factories.EntityFactory;
import com.finpro.pools.DiamondPool;
import java.util.ArrayList;
import java.util.List;

public class GameFacade {
    private Player fireGirl;
    private Player waterBoy;
    private List<Platform> platforms;
    private List<Obstacle> obstacles;
    private List<Box> boxes;
    private List<Key> keys;
    private Door door;
    private DiamondPool diamondPool;
    private Texture background;

    private int level;
    private boolean gameOver;
    private int keysCollected;
    private int totalKeys;
    private float spawnProtectionTimer; // Grace period at start
    private static final float SPAWN_PROTECTION_TIME = 0.5f;

    public GameFacade(int level) {
        this.level = level;
        this.platforms = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.boxes = new ArrayList<>();
        this.keys = new ArrayList<>();
        this.diamondPool = new DiamondPool();
        this.gameOver = false;
        this.keysCollected = 0;
        this.spawnProtectionTimer = SPAWN_PROTECTION_TIME; // Start with protection

        background = new Texture("background.png");
        initializeLevel();
    }

    private void initializeLevel() {
        // Create ground FIRST - full width
        platforms.add(EntityFactory.createPlatform(0, 30, Main.WORLD_WIDTH));

        // Level-specific setup
        switch (level) {
            case 1: setupEasyLevel(); break;
            case 2: setupMediumLevel(); break;
            case 3: setupHardLevel(); break;
        }

        // Create players AFTER platforms (so they spawn safely)
        fireGirl = EntityFactory.createFireGirl(50, 80);
        waterBoy = EntityFactory.createWaterBoy(120, 80);

        // Set them on ground immediately
        fireGirl.setY(50); // Ground height (30) + platform height (20)
        waterBoy.setY(50);
        fireGirl.setOnGround(true);
        waterBoy.setOnGround(true);

        totalKeys = keys.size();

        System.out.println("Level " + level + " initialized!");
        System.out.println("Platforms: " + platforms.size());
        System.out.println("Obstacles: " + obstacles.size());
        System.out.println("Keys: " + keys.size());
    }

    private void setupEasyLevel() {
        // Lower platforms
        platforms.add(EntityFactory.createPlatform(200, 150, 250));
        platforms.add(EntityFactory.createPlatform(550, 180, 200));
        platforms.add(EntityFactory.createPlatform(850, 150, 200));
        platforms.add(EntityFactory.createPlatform(1150, 180, 250));

        // Middle platforms
        platforms.add(EntityFactory.createPlatform(100, 300, 200));
        platforms.add(EntityFactory.createPlatform(400, 330, 250));
        platforms.add(EntityFactory.createPlatform(750, 300, 200));
        platforms.add(EntityFactory.createPlatform(1050, 330, 200));
        platforms.add(EntityFactory.createPlatform(1350, 300, 200));

        // Upper platforms
        platforms.add(EntityFactory.createPlatform(250, 480, 200));
        platforms.add(EntityFactory.createPlatform(600, 510, 250));
        platforms.add(EntityFactory.createPlatform(950, 480, 200));
        platforms.add(EntityFactory.createPlatform(1250, 510, 200));

        // Top platform for door
        platforms.add(EntityFactory.createPlatform(700, 700, 200));

        // Diamonds - distributed across the level
        diamondPool.obtain(250, 175, Diamond.DiamondType.RED);
        diamondPool.obtain(600, 205, Diamond.DiamondType.BLUE);
        diamondPool.obtain(900, 175, Diamond.DiamondType.RED);
        diamondPool.obtain(1200, 205, Diamond.DiamondType.BLUE);

        diamondPool.obtain(150, 325, Diamond.DiamondType.RED);
        diamondPool.obtain(500, 355, Diamond.DiamondType.BLUE);
        diamondPool.obtain(800, 325, Diamond.DiamondType.RED);
        diamondPool.obtain(1100, 355, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1400, 325, Diamond.DiamondType.RED);

        diamondPool.obtain(300, 505, Diamond.DiamondType.BLUE);
        diamondPool.obtain(700, 535, Diamond.DiamondType.RED);
        diamondPool.obtain(1000, 505, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1300, 535, Diamond.DiamondType.RED);

        // Obstacles
        obstacles.add(EntityFactory.createObstacle(450, 50, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(700, 50, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1000, 50, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(300, 180, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(950, 330, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(500, 510, Obstacle.ObstacleType.WATER));

        // Movable boxes
        boxes.add(EntityFactory.createBox(650, 80)); // Adjusted Y to sit on ground
        boxes.add(EntityFactory.createBox(350, 180));
        boxes.add(EntityFactory.createBox(1100, 80));

        // Keys
        keys.add(EntityFactory.createKey(320, 505));
        keys.add(EntityFactory.createKey(1300, 330));

        // Door at the top
        door = EntityFactory.createDoor(770, 730);
    }

    private void setupMediumLevel() {
        // Complex multi-level platforming
        // Lower level
        platforms.add(EntityFactory.createPlatform(250, 120, 180)); // Away from spawn
        platforms.add(EntityFactory.createPlatform(500, 150, 150));
        platforms.add(EntityFactory.createPlatform(720, 130, 180));
        platforms.add(EntityFactory.createPlatform(970, 160, 150));
        platforms.add(EntityFactory.createPlatform(1190, 140, 200));
        platforms.add(EntityFactory.createPlatform(1450, 150, 130));

        // Mid-low level
        platforms.add(EntityFactory.createPlatform(120, 260, 160));
        platforms.add(EntityFactory.createPlatform(350, 290, 180));
        platforms.add(EntityFactory.createPlatform(600, 270, 150));
        platforms.add(EntityFactory.createPlatform(820, 300, 170));
        platforms.add(EntityFactory.createPlatform(1060, 280, 180));
        platforms.add(EntityFactory.createPlatform(1310, 290, 150));

        // Mid-high level
        platforms.add(EntityFactory.createPlatform(200, 420, 150));
        platforms.add(EntityFactory.createPlatform(450, 450, 200));
        platforms.add(EntityFactory.createPlatform(730, 430, 160));
        platforms.add(EntityFactory.createPlatform(980, 460, 180));
        platforms.add(EntityFactory.createPlatform(1240, 440, 150));
        platforms.add(EntityFactory.createPlatform(1460, 450, 120));

        // Upper level
        platforms.add(EntityFactory.createPlatform(100, 590, 180));
        platforms.add(EntityFactory.createPlatform(380, 620, 200));
        platforms.add(EntityFactory.createPlatform(680, 600, 150));
        platforms.add(EntityFactory.createPlatform(930, 630, 180));
        platforms.add(EntityFactory.createPlatform(1200, 610, 200));
        platforms.add(EntityFactory.createPlatform(1480, 620, 100));

        // Top platform for door
        platforms.add(EntityFactory.createPlatform(700, 780, 200));

        // Many diamonds
        diamondPool.obtain(300, 145, Diamond.DiamondType.RED);
        diamondPool.obtain(550, 175, Diamond.DiamondType.BLUE);
        diamondPool.obtain(770, 155, Diamond.DiamondType.RED);
        diamondPool.obtain(1020, 185, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1240, 165, Diamond.DiamondType.RED);
        diamondPool.obtain(1500, 175, Diamond.DiamondType.BLUE);

        diamondPool.obtain(170, 285, Diamond.DiamondType.RED);
        diamondPool.obtain(430, 315, Diamond.DiamondType.BLUE);
        diamondPool.obtain(650, 295, Diamond.DiamondType.RED);
        diamondPool.obtain(890, 325, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1140, 305, Diamond.DiamondType.RED);
        diamondPool.obtain(1360, 315, Diamond.DiamondType.BLUE);

        diamondPool.obtain(250, 445, Diamond.DiamondType.RED);
        diamondPool.obtain(550, 475, Diamond.DiamondType.BLUE);
        diamondPool.obtain(780, 455, Diamond.DiamondType.RED);
        diamondPool.obtain(1060, 485, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1290, 465, Diamond.DiamondType.RED);

        diamondPool.obtain(150, 615, Diamond.DiamondType.BLUE);
        diamondPool.obtain(480, 645, Diamond.DiamondType.RED);
        diamondPool.obtain(730, 625, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1010, 655, Diamond.DiamondType.RED);
        diamondPool.obtain(1300, 635, Diamond.DiamondType.BLUE);

        // Obstacles on platforms - AWAY from spawn (x > 250)
        obstacles.add(EntityFactory.createObstacle(400, 50, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(650, 50, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(900, 50, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1150, 50, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1380, 50, Obstacle.ObstacleType.FIRE));

        obstacles.add(EntityFactory.createObstacle(450, 150, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(920, 160, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(280, 290, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(750, 300, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1190, 280, Obstacle.ObstacleType.WATER));

        obstacles.add(EntityFactory.createObstacle(360, 450, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(830, 460, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1350, 440, Obstacle.ObstacleType.FIRE));

        // Movable boxes for puzzle solving
        boxes.add(EntityFactory.createBox(400, 80));
        boxes.add(EntityFactory.createBox(700, 180));
        boxes.add(EntityFactory.createBox(500, 330));
        boxes.add(EntityFactory.createBox(1000, 210));
        boxes.add(EntityFactory.createBox(1300, 80));

        // Keys
        keys.add(EntityFactory.createKey(250, 445));
        keys.add(EntityFactory.createKey(1010, 655));
        keys.add(EntityFactory.createKey(480, 645));

        // Door
        door = EntityFactory.createDoor(770, 810);
    }

    private void setupHardLevel() {
        // Very complex layout with tight jumps
        // Bottom scattered platforms - START AWAY from spawn
        for (int i = 2; i < 10; i++) { // Start from i=2 to avoid spawn area
            float x = 50 + i * 160;
            float y = 120 + (i % 3) * 20;
            float width = 100 + (i % 2) * 50;
            platforms.add(EntityFactory.createPlatform(x, y, width));
        }

        // Second layer
        for (int i = 1; i < 9; i++) {
            float x = 100 + i * 170;
            float y = 250 + (i % 3) * 25;
            float width = 110 + (i % 2) * 40;
            platforms.add(EntityFactory.createPlatform(x, y, width));
        }

        // Third layer
        for (int i = 0; i < 8; i++) {
            float x = 150 + i * 180;
            float y = 390 + (i % 3) * 20;
            float width = 120 + (i % 2) * 30;
            platforms.add(EntityFactory.createPlatform(x, y, width));
        }

        // Fourth layer
        for (int i = 0; i < 7; i++) {
            float x = 200 + i * 200;
            float y = 530 + (i % 2) * 30;
            float width = 130;
            platforms.add(EntityFactory.createPlatform(x, y, width));
        }

        // Fifth layer
        for (int i = 0; i < 6; i++) {
            float x = 250 + i * 220;
            float y = 670 + (i % 2) * 20;
            float width = 140;
            platforms.add(EntityFactory.createPlatform(x, y, width));
        }

        // Top platform for door
        platforms.add(EntityFactory.createPlatform(700, 820, 200));

        // Massive amount of diamonds
        for (int i = 3; i < 30; i++) {
            float x = 80 + (i % 10) * 150;
            float y = 145 + (i / 10) * 140;
            Diamond.DiamondType type = (i % 2 == 0) ? Diamond.DiamondType.RED : Diamond.DiamondType.BLUE;
            diamondPool.obtain(x, y, type);
        }

        // Many obstacles - AWAY from spawn
        for (int i = 3; i < 20; i++) { // Start from i=3
            float x = 100 + (i % 8) * 180;
            float y = 50 + (i / 8) * 140;
            Obstacle.ObstacleType type = (i % 2 == 0) ? Obstacle.ObstacleType.FIRE : Obstacle.ObstacleType.WATER;
            obstacles.add(EntityFactory.createObstacle(x, y, type));
        }

        // Many boxes for complex puzzles
        boxes.add(EntityFactory.createBox(500, 80));
        boxes.add(EntityFactory.createBox(700, 80));
        boxes.add(EntityFactory.createBox(900, 80));
        boxes.add(EntityFactory.createBox(1200, 80));
        boxes.add(EntityFactory.createBox(400, 180));
        boxes.add(EntityFactory.createBox(800, 180));
        boxes.add(EntityFactory.createBox(1100, 180));
        boxes.add(EntityFactory.createBox(500, 300));
        boxes.add(EntityFactory.createBox(1000, 300));

        // Multiple keys
        keys.add(EntityFactory.createKey(500, 420));
        keys.add(EntityFactory.createKey(800, 560));
        keys.add(EntityFactory.createKey(1200, 420));
        keys.add(EntityFactory.createKey(600, 700));

        // Door
        door = EntityFactory.createDoor(770, 850);
    }

    public void updateGame(float delta) {
        if (gameOver) return;

        // Update spawn protection timer
        if (spawnProtectionTimer > 0) {
            spawnProtectionTimer -= delta;
        }

        fireGirl.update(delta);
        waterBoy.update(delta);

        // Update boxes
        for (Box box : boxes) {
            box.update(delta);
        }

        // Update keys
        for (Key key : keys) {
            key.update(delta);
        }

        handleCollisions();

        // Check if players died (only after spawn protection)
        if (spawnProtectionTimer <= 0 && (fireGirl.isDead() || waterBoy.isDead())) {
            System.out.println("Player death detected in updateGame");
            gameOver = true;
        }
    }

    public void renderGame(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        batch.draw(background, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        batch.end();

        for (Platform platform : platforms) {
            platform.render(shapeRenderer);
        }

        for (Box box : boxes) {
            box.render(shapeRenderer);
        }

        door.render(shapeRenderer);

        // Render keys with shape renderer (backup if texture not found)
        for (Key key : keys) {
            key.renderShape(shapeRenderer);
        }

        batch.begin();

        for (Obstacle obstacle : obstacles) {
            obstacle.render(batch);
        }

        for (Diamond diamond : diamondPool.getInUse()) {
            diamond.render(batch);
        }

        // Render keys with texture if available
        for (Key key : keys) {
            key.render(batch);
        }

        fireGirl.render(batch);
        waterBoy.render(batch);

        batch.end();
    }

    private void handleCollisions() {
        handlePlayerPlatformCollisions(fireGirl);
        handlePlayerPlatformCollisions(waterBoy);
        handleBoxCollisions();
        handlePlayerBoxCollisions();
        handleDiamondCollisions();
        handleKeyCollisions();
        handleObstacleCollisions();
    }

    private void handlePlayerPlatformCollisions(Player player) {
        boolean onPlatform = false;
        Rectangle pBounds = player.getBounds();

        for (Platform platform : platforms) {
            Rectangle platBounds = platform.getBounds();

            if (pBounds.overlaps(platBounds)) {
                if (player.getVelocityY() <= 0) {
                    float playerBottom = pBounds.y;
                    float platformTop = platBounds.y + platBounds.height;

                    if (playerBottom >= platformTop - 15 && playerBottom <= platformTop + 2) {
                        float overlap = platformTop - playerBottom;

                        if (Math.abs(overlap) < 20) {
                            player.setY(platformTop);
                            player.setOnGround(true);
                            onPlatform = true;
                            break;
                        }
                    }
                } else if (player.getVelocityY() > 0) {
                    float playerTop = pBounds.y + pBounds.height;
                    float platformBottom = platBounds.y;

                    if (playerTop >= platformBottom - 2 && playerTop <= platformBottom + 15) {
                        player.setVelocityY(-50);
                    }
                }
            }
        }

        if (!onPlatform) {
            player.setOnGround(false);
        }
    }

    private void handleBoxCollisions() {
        for (Box box : boxes) {
            boolean onPlatform = false;
            Rectangle boxBounds = box.getBounds();

            for (Platform platform : platforms) {
                Rectangle platBounds = platform.getBounds();

                if (boxBounds.overlaps(platBounds)) {
                    if (box.getVelocityY() <= 0) {
                        float boxBottom = boxBounds.y;
                        float platformTop = platBounds.y + platBounds.height;

                        if (boxBottom >= platformTop - 15 && boxBottom <= platformTop + 2) {
                            box.setY(platformTop);
                            box.setOnGround(true);
                            onPlatform = true;
                            break;
                        }
                    } else if (box.getVelocityY() > 0) {
                        float boxTop = boxBounds.y + boxBounds.height;
                        float platformBottom = platBounds.y;

                        if (boxTop >= platformBottom - 2 && boxTop <= platformBottom + 15) {
                            box.setVelocityY(-50);
                        }
                    }
                }
            }

            if (!onPlatform) {
                box.setOnGround(false);
            }
        }
    }

    private void handlePlayerBoxCollisions() {
        handlePlayerBoxInteraction(fireGirl);
        handlePlayerBoxInteraction(waterBoy);
    }

    private void handlePlayerBoxInteraction(Player player) {
        Rectangle pBounds = player.getBounds();

        for (Box box : boxes) {
            Rectangle boxBounds = box.getBounds();

            if (pBounds.overlaps(boxBounds)) {
                // Player landing on box
                if (player.getVelocityY() <= 0) {
                    float playerBottom = pBounds.y;
                    float boxTop = boxBounds.y + boxBounds.height;

                    if (playerBottom >= boxTop - 15 && playerBottom <= boxTop + 2) {
                        player.setY(boxTop);
                        player.setOnGround(true);
                    }
                }

                // Player pushing box horizontally
                float playerCenterX = pBounds.x + pBounds.width / 2;
                float boxCenterX = boxBounds.x + boxBounds.width / 2;

                if (Math.abs(pBounds.y - boxBounds.y) < 20) {
                    if (playerCenterX < boxCenterX) {
                        box.pushRight();
                    } else {
                        box.pushLeft();
                    }
                }
            }
        }
    }

    private void handleDiamondCollisions() {
        for (Diamond diamond : diamondPool.getInUse()) {
            if (!diamond.isCollected()) {
                Rectangle expandedFireGirl = new Rectangle(
                    fireGirl.getBounds().x - 5,
                    fireGirl.getBounds().y - 5,
                    fireGirl.getBounds().width + 10,
                    fireGirl.getBounds().height + 10
                );

                Rectangle expandedWaterBoy = new Rectangle(
                    waterBoy.getBounds().x - 5,
                    waterBoy.getBounds().y - 5,
                    waterBoy.getBounds().width + 10,
                    waterBoy.getBounds().height + 10
                );

                if (diamond.getType() == Diamond.DiamondType.RED &&
                    expandedFireGirl.overlaps(diamond.getBounds())) {
                    diamond.collect();
                } else if (diamond.getType() == Diamond.DiamondType.BLUE &&
                    expandedWaterBoy.overlaps(diamond.getBounds())) {
                    diamond.collect();
                }
            }
        }
    }

    private void handleKeyCollisions() {
        for (Key key : keys) {
            if (!key.isCollected()) {
                if (fireGirl.getBounds().overlaps(key.getBounds()) ||
                    waterBoy.getBounds().overlaps(key.getBounds())) {
                    key.collect();
                    keysCollected++;

                    // Unlock door when all keys collected
                    if (keysCollected >= totalKeys) {
                        door.unlock();
                    }
                }
            }
        }
    }

    private void handleObstacleCollisions() {
        if (spawnProtectionTimer > 0) return;

        if (fireGirl.isDead() || waterBoy.isDead()) return; // Already dead

        for (Obstacle obstacle : obstacles) {
            Rectangle fireGirlHitbox = new Rectangle(
                fireGirl.getBounds().x + 8,
                fireGirl.getBounds().y + 8,
                fireGirl.getBounds().width - 16,
                fireGirl.getBounds().height - 16
            );

            Rectangle waterBoyHitbox = new Rectangle(
                waterBoy.getBounds().x + 8,
                waterBoy.getBounds().y + 8,
                waterBoy.getBounds().width - 16,
                waterBoy.getBounds().height - 16
            );

            if (obstacle.getType() == Obstacle.ObstacleType.WATER &&
                fireGirlHitbox.overlaps(obstacle.getBounds())) {
                fireGirl.die();
                System.out.println("FireGirl touched WATER! Game Over!");
            }

            if (obstacle.getType() == Obstacle.ObstacleType.FIRE &&
                waterBoyHitbox.overlaps(obstacle.getBounds())) {
                waterBoy.die();
                System.out.println("WaterBoy touched LAVA! Game Over!");
            }
        }
    }

    public boolean checkLevelComplete() {
        if (gameOver || door.isLocked()) return false;

        boolean allDiamondsCollected = true;
        for (Diamond diamond : diamondPool.getInUse()) {
            if (!diamond.isCollected()) {
                allDiamondsCollected = false;
                break;
            }
        }

        boolean bothAtDoor = fireGirl.getBounds().overlaps(door.getBounds()) &&
            waterBoy.getBounds().overlaps(door.getBounds());

        return allDiamondsCollected && bothAtDoor;
    }

    public Player getFireGirl() { return fireGirl; }
    public Player getWaterBoy() { return waterBoy; }
    public boolean isGameOver() { return gameOver; }
    public int getKeysCollected() { return keysCollected; }
    public int getTotalKeys() { return totalKeys; }
    public boolean isDoorUnlocked() { return !door.isLocked(); }

    public void dispose() {
        diamondPool.freeAll();
        fireGirl.dispose();
        waterBoy.dispose();
        background.dispose();
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }
        for (Key key : keys) {
            key.dispose();
        }
    }
}
