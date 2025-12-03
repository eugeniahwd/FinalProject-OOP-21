package com.finpro.facade;

import com.badlogic.gdx.Gdx;
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

/**
 * FACADE PATTERN - With Safe Diamond Placement
 */
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
    private float spawnProtectionTimer;
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
        this.spawnProtectionTimer = SPAWN_PROTECTION_TIME;

        background = new Texture("background.png");
        initializeLevel();
    }

    private void initializeLevel() {
        platforms.add(EntityFactory.createPlatform(0, 30, Main.WORLD_WIDTH, false));

        switch (level) {
            case 1: setupEasyLevel(); break;
            case 2: setupMediumLevel(); break;
            case 3: setupHardLevel(); break;
        }

        fireGirl = EntityFactory.createFireGirl(50, 80);
        waterBoy = EntityFactory.createWaterBoy(120, 80);

        fireGirl.setY(50);
        waterBoy.setY(50);
        fireGirl.setOnGround(true);
        waterBoy.setOnGround(true);

        totalKeys = keys.size();

        System.out.println("Level " + level + " initialized!");
        System.out.println("Platforms: " + platforms.size());
        System.out.println("Moving Boxes: " + boxes.stream().filter(Box::isMovingPlatform).count());
        System.out.println("Diamonds: " + diamondPool.getInUse().size());
        System.out.println("Obstacles: " + obstacles.size());
    }

    private void setupEasyLevel() {
        // Lower platforms
        platforms.add(EntityFactory.createPlatform(200, 150, 200, true));
        platforms.add(EntityFactory.createPlatform(650, 150, 400, false));
        platforms.add(EntityFactory.createPlatform(1100, 150, 350, false));

        // Middle platforms
        platforms.add(EntityFactory.createPlatform(70, 330, 150, false));
        platforms.add(EntityFactory.createPlatform(700, 330, 150, false));
        platforms.add(EntityFactory.createPlatform(1200, 330, 200, false));

        // Upper platforms
        platforms.add(EntityFactory.createPlatform(120, 500, 480, false));
        platforms.add(EntityFactory.createPlatform(330, 520, 330, true));
        platforms.add(EntityFactory.createPlatform(900, 570, 200, false));
        platforms.add(EntityFactory.createPlatform(1300, 550, 150, false));

        // Top platform
        platforms.add(EntityFactory.createPlatform(700, 750, 200, false));

        // MOVING BOXES
        boxes.add(EntityFactory.createMovingBox(300, 330, 250, 650));
        boxes.add(EntityFactory.createMovingBox(850, 500, 600, 850));

        // OBSTACLES FIRST - Placed strategically
        // Ground level - spread out
        obstacles.add(EntityFactory.createObstacle(550, 50, Obstacle.ObstacleType.FIRE));      // Far right on ground
        obstacles.add(EntityFactory.createObstacle(900, 50, Obstacle.ObstacleType.WATER));     // Even further

        // Lower platforms - one per platform, far from edges
        obstacles.add(EntityFactory.createObstacle(250, 180, Obstacle.ObstacleType.WATER));    // On platform 200
        obstacles.add(EntityFactory.createObstacle(1150, 180, Obstacle.ObstacleType.FIRE));    // On platform 1100

        // Middle level - sparse placement
        obstacles.add(EntityFactory.createObstacle(1250, 360, Obstacle.ObstacleType.WATER));   // On platform 1200

        // DIAMONDS - Safe positions, away from obstacles
        // Lower platforms - safe spots
        diamondPool.obtain(120, 175, Diamond.DiamondType.RED);      // Left side of platform 200 (far from water at 250)
        diamondPool.obtain(370, 175, Diamond.DiamondType.RED);      // Right side of platform 200
        diamondPool.obtain(670, 185, Diamond.DiamondType.BLUE);     // Left side of platform 650 (safe)
        diamondPool.obtain(820, 185, Diamond.DiamondType.BLUE);     // Right side of platform 650
        diamondPool.obtain(1120, 175, Diamond.DiamondType.RED);     // Left edge platform 1100 (fire is at 1150)

        // Middle platforms - safe spots
        diamondPool.obtain(120, 355, Diamond.DiamondType.RED);      // Platform 100 (safe)
        diamondPool.obtain(720, 375, Diamond.DiamondType.BLUE);     // Platform 700 (safe)
        diamondPool.obtain(820, 375, Diamond.DiamondType.BLUE);     // Platform 700 right
        diamondPool.obtain(1220, 355, Diamond.DiamondType.RED);     // Platform 1200 left (water is at 1250)
        diamondPool.obtain(1370, 355, Diamond.DiamondType.RED);     // Platform 1200 right

        // Upper platforms - all safe
        diamondPool.obtain(280, 575, Diamond.DiamondType.BLUE);     // Platform 250
        diamondPool.obtain(420, 575, Diamond.DiamondType.BLUE);     // Platform 250 right
        diamondPool.obtain(920, 595, Diamond.DiamondType.RED);      // Platform 900
        diamondPool.obtain(1070, 595, Diamond.DiamondType.RED);     // Platform 900 right
        diamondPool.obtain(1320, 575, Diamond.DiamondType.BLUE);    // Platform 1300

        // Keys - safe positions
        keys.add(EntityFactory.createKey(400, 575));
        keys.add(EntityFactory.createKey(1360, 355));

        door = EntityFactory.createDoor(770, 780);
    }

    private void setupMediumLevel() {
        // Platform layers
        platforms.add(EntityFactory.createPlatform(100, 120, 180, false));
        platforms.add(EntityFactory.createPlatform(500, 150, 150, false));
        platforms.add(EntityFactory.createPlatform(900, 130, 180, false));
        platforms.add(EntityFactory.createPlatform(1300, 150, 200, false));

        platforms.add(EntityFactory.createPlatform(150, 280, 160, false));
        platforms.add(EntityFactory.createPlatform(600, 300, 150, false));
        platforms.add(EntityFactory.createPlatform(1000, 280, 180, false));
        platforms.add(EntityFactory.createPlatform(1400, 290, 150, false));

        platforms.add(EntityFactory.createPlatform(200, 450, 150, false));
        platforms.add(EntityFactory.createPlatform(700, 470, 160, false));
        platforms.add(EntityFactory.createPlatform(1100, 460, 180, false));

        platforms.add(EntityFactory.createPlatform(300, 620, 180, false));
        platforms.add(EntityFactory.createPlatform(800, 640, 150, false));
        platforms.add(EntityFactory.createPlatform(1200, 630, 200, false));

        platforms.add(EntityFactory.createPlatform(700, 800, 200, false));

        // Moving boxes
        boxes.add(EntityFactory.createMovingBox(290, 180, 280, 450));
        boxes.add(EntityFactory.createMovingBox(650, 200, 500, 850));
        boxes.add(EntityFactory.createMovingBox(1090, 190, 900, 1250));

        boxes.add(EntityFactory.createMovingBox(310, 350, 150, 550));
        boxes.add(EntityFactory.createMovingBox(750, 370, 600, 950));
        boxes.add(EntityFactory.createMovingBox(1190, 360, 1000, 1350));

        boxes.add(EntityFactory.createMovingBox(350, 520, 200, 650));
        boxes.add(EntityFactory.createMovingBox(850, 540, 700, 1050));

        // OBSTACLES - Strategic placement
        // Ground level
        obstacles.add(EntityFactory.createObstacle(600, 50, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1000, 50, Obstacle.ObstacleType.WATER));

        // Lower platforms - center of platforms
        obstacles.add(EntityFactory.createObstacle(540, 180, Obstacle.ObstacleType.WATER));    // Platform 500
        obstacles.add(EntityFactory.createObstacle(950, 160, Obstacle.ObstacleType.FIRE));     // Platform 900

        // Mid platforms
        obstacles.add(EntityFactory.createObstacle(640, 330, Obstacle.ObstacleType.FIRE));     // Platform 600
        obstacles.add(EntityFactory.createObstacle(1050, 310, Obstacle.ObstacleType.WATER));   // Platform 1000

        // Upper platforms
        obstacles.add(EntityFactory.createObstacle(740, 500, Obstacle.ObstacleType.WATER));    // Platform 700
        obstacles.add(EntityFactory.createObstacle(1140, 490, Obstacle.ObstacleType.FIRE));    // Platform 1100

        // DIAMONDS - Safe positions away from obstacles
        // Layer 1 - safe edges
        diamondPool.obtain(120, 145, Diamond.DiamondType.RED);      // Platform 100 left
        diamondPool.obtain(250, 145, Diamond.DiamondType.RED);      // Platform 100 right
        diamondPool.obtain(510, 175, Diamond.DiamondType.BLUE);     // Platform 500 left (obstacle at 540)
        diamondPool.obtain(920, 155, Diamond.DiamondType.RED);      // Platform 900 left (obstacle at 950)
        diamondPool.obtain(1320, 175, Diamond.DiamondType.BLUE);    // Platform 1300 left
        diamondPool.obtain(1470, 175, Diamond.DiamondType.BLUE);    // Platform 1300 right

        // Layer 2 - safe edges
        diamondPool.obtain(170, 305, Diamond.DiamondType.RED);      // Platform 150
        diamondPool.obtain(280, 305, Diamond.DiamondType.RED);      // Platform 150 right
        diamondPool.obtain(610, 325, Diamond.DiamondType.BLUE);     // Platform 600 left (obstacle at 640)
        diamondPool.obtain(1020, 305, Diamond.DiamondType.RED);     // Platform 1000 left (obstacle at 1050)
        diamondPool.obtain(1420, 315, Diamond.DiamondType.BLUE);    // Platform 1400

        // Layer 3 - safe edges
        diamondPool.obtain(220, 475, Diamond.DiamondType.BLUE);     // Platform 200
        diamondPool.obtain(710, 495, Diamond.DiamondType.RED);      // Platform 700 left (obstacle at 740)
        diamondPool.obtain(1120, 485, Diamond.DiamondType.BLUE);    // Platform 1100 left (obstacle at 1140)
        diamondPool.obtain(1250, 485, Diamond.DiamondType.BLUE);    // Platform 1100 right

        // Layer 4 - all safe
        diamondPool.obtain(320, 645, Diamond.DiamondType.RED);      // Platform 300
        diamondPool.obtain(450, 645, Diamond.DiamondType.RED);      // Platform 300 right
        diamondPool.obtain(820, 665, Diamond.DiamondType.BLUE);     // Platform 800
        diamondPool.obtain(1220, 655, Diamond.DiamondType.RED);     // Platform 1200
        diamondPool.obtain(1370, 655, Diamond.DiamondType.RED);     // Platform 1200 right

        // Keys - safe positions
        keys.add(EntityFactory.createKey(320, 475));
        keys.add(EntityFactory.createKey(820, 665));
        keys.add(EntityFactory.createKey(1370, 305));

        door = EntityFactory.createDoor(770, 830);
    }

    private void setupHardLevel() {
        // Many platform layers
        platforms.add(EntityFactory.createPlatform(0, 200, 400, false));

        platforms.add(EntityFactory.createPlatform(480, 30, 110, true));
        platforms.add(EntityFactory.createPlatform(500, 120, 100, false));
        platforms.add(EntityFactory.createPlatform(600, 30, 110, true));

        platforms.add(EntityFactory.createPlatform(720, 30, 110, true));
        platforms.add(EntityFactory.createPlatform(740, 120, 100, false));
        platforms.add(EntityFactory.createPlatform(840, 30, 110, true));

        platforms.add(EntityFactory.createPlatform(960, 30, 110, true));
        platforms.add(EntityFactory.createPlatform(980, 120, 100, false));
        platforms.add(EntityFactory.createPlatform(1080, 30, 110, true));


        // obstacles
        obstacles.add(EntityFactory.createObstacle(570, 23, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(810, 23, Obstacle.ObstacleType.WATER));

        obstacles.add(EntityFactory.createObstacle(1040, 23, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1200, 23, Obstacle.ObstacleType.WATER));

        obstacles.add(EntityFactory.createObstacle(1360, 23, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1440, 23, Obstacle.ObstacleType.WATER));

        // yang ada diamond merah 2
        obstacles.add(EntityFactory.createObstacle(1520, 23, Obstacle.ObstacleType.FIRE));
        // yang ada diamond biru 2
        obstacles.add(EntityFactory.createObstacle(1600, 23, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1680, 23, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1540, 23, Obstacle.ObstacleType.WATER));

        // diamonds
        //ground:
        diamondPool.obtain(1200, 100, Diamond.DiamondType.RED);
        diamondPool.obtain(1400, 100, Diamond.DiamondType.RED);
        diamondPool.obtain(1250, 100, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1450, 100, Diamond.DiamondType.BLUE);



        // Keys
        // keys 1 (ground)
        keys.add(EntityFactory.createKey(1550, 100));

        keys.add(EntityFactory.createKey(750, 625));
        keys.add(EntityFactory.createKey(410, 625));
        keys.add(EntityFactory.createKey(1210, 485));

        door = EntityFactory.createDoor(770, 830);
    }

    public void updateGame(float delta) {
        if (gameOver) return;

        if (spawnProtectionTimer > 0) {
            spawnProtectionTimer -= delta;
        }

        fireGirl.update(delta);
        waterBoy.update(delta);

        for (Box box : boxes) {
            box.update(delta);
        }

        for (Key key : keys) {
            key.update(delta);
        }

        handleCollisions();

        if (spawnProtectionTimer <= 0 && (fireGirl.isDead() || waterBoy.isDead())) {
            System.out.println("Player death detected!");
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
                // COLLISION DARI ATAS (Landing)
                if (player.getVelocityY() <= 0) {
                    float playerBottom = pBounds.y;
                    float platformTop = platBounds.y + platBounds.height;

                    if (playerBottom >= platformTop - 15 && playerBottom <= platformTop + 2) {
                        float overlap = platformTop - playerBottom;

                        if (Math.abs(overlap) < 20) {
                            player.setY(platformTop);
                            player.setOnGround(true);
                            onPlatform = true;
                            continue; // Skip horizontal check jika sudah landing
                        }
                    }
                }
                // COLLISION DARI BAWAH (Head bump)
                else if (player.getVelocityY() > 0) {
                    float playerTop = pBounds.y + pBounds.height;
                    float platformBottom = platBounds.y;

                    if (playerTop >= platformBottom - 2 && playerTop <= platformBottom + 15) {
                        player.setVelocityY(-50);
                        continue; // Skip horizontal check
                    }
                }

                // ===== TAMBAHAN: HORIZONTAL COLLISION =====
                // Cek collision dari kiri atau kanan
                float playerCenterY = pBounds.y + pBounds.height / 2;
                float platformCenterY = platBounds.y + platBounds.height / 2;

                // Hanya cek horizontal jika player tidak sedang landing/head bump
                if (Math.abs(playerCenterY - platformCenterY) < platBounds.height / 2) {
                    float playerRight = pBounds.x + pBounds.width;
                    float playerLeft = pBounds.x;
                    float platformRight = platBounds.x + platBounds.width;
                    float platformLeft = platBounds.x;

                    // Collision dari KIRI (player nabrak sisi kiri platform)
                    if (playerRight > platformLeft && playerLeft < platformLeft) {
                        player.setX(platformLeft - pBounds.width);
                        player.setVelocityX(0); // Stop horizontal movement
                    }
                    // Collision dari KANAN (player nabrak sisi kanan platform)
                    else if (playerLeft < platformRight && playerRight > platformRight) {
                        player.setX(platformRight);
                        player.setVelocityX(0); // Stop horizontal movement
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
            if (box.isMovingPlatform()) {
                continue;
            }

            boolean onPlatform = false;
            Rectangle boxBounds = box.getBounds();

            for (Platform platform : platforms) {
                Rectangle platBounds = platform.getBounds();

                if (boxBounds.overlaps(platBounds)) {
                    // ===== VERTICAL COLLISION (sudah ada) =====
                    if (box.getVelocityY() <= 0) {
                        float boxBottom = boxBounds.y;
                        float platformTop = platBounds.y + platBounds.height;

                        if (boxBottom >= platformTop - 15 && boxBottom <= platformTop + 2) {
                            box.setY(platformTop);
                            box.setOnGround(true);
                            onPlatform = true;
                            continue; // Skip horizontal check jika sudah landing
                        }
                    } else if (box.getVelocityY() > 0) {
                        float boxTop = boxBounds.y + boxBounds.height;
                        float platformBottom = platBounds.y;

                        if (boxTop >= platformBottom - 2 && boxTop <= platformBottom + 15) {
                            box.setVelocityY(-50);
                            continue; // Skip horizontal check
                        }
                    }

                    // ===== HORIZONTAL COLLISION (BARU) =====
                    float boxCenterY = boxBounds.y + boxBounds.height / 2;
                    float platformCenterY = platBounds.y + platBounds.height / 2;

                    // Hanya cek horizontal jika box tidak sedang landing/bouncing
                    if (Math.abs(boxCenterY - platformCenterY) < platBounds.height / 2) {
                        float boxRight = boxBounds.x + boxBounds.width;
                        float boxLeft = boxBounds.x;
                        float platformRight = platBounds.x + platBounds.width;
                        float platformLeft = platBounds.x;

                        // Collision dari KIRI (box nabrak sisi kiri platform)
                        if (boxRight > platformLeft && boxLeft < platformLeft) {
                            box.setX(platformLeft - boxBounds.width);
                            // Tidak perlu setVelocityX karena regular box tidak bergerak horizontal
                        }
                        // Collision dari KANAN (box nabrak sisi kanan platform)
                        else if (boxLeft < platformRight && boxRight > platformRight) {
                            box.setX(platformRight);
                            // Tidak perlu setVelocityX karena regular box tidak bergerak horizontal
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
                // Hitung overlap di setiap sisi
                float overlapLeft = (pBounds.x + pBounds.width) - boxBounds.x;
                float overlapRight = (boxBounds.x + boxBounds.width) - pBounds.x;
                float overlapTop = (pBounds.y + pBounds.height) - boxBounds.y;
                float overlapBottom = (boxBounds.y + boxBounds.height) - pBounds.y;

                // Cari overlap terkecil (arah tabrakan yang paling baru terjadi)
                float minOverlap = Math.min(
                    Math.min(overlapLeft, overlapRight),
                    Math.min(overlapTop, overlapBottom)
                );

                // ===== COLLISION DARI ATAS (Player landing di box) =====
                if (minOverlap == overlapBottom && player.getVelocityY() <= 0) {
                    player.setY(boxBounds.y + boxBounds.height);
                    player.setOnGround(true);

                    // Jika box bergerak, player ikut bergerak
                    if (box.isMovingPlatform()) {
                        float deltaX = box.getVelocityX() * Gdx.graphics.getDeltaTime();
                        player.setX(player.getX() + deltaX);
                    }
                }
                // ===== COLLISION DARI BAWAH (Player nabrak bawah box) =====
                else if (minOverlap == overlapTop && player.getVelocityY() > 0) {
                    player.setY(boxBounds.y - pBounds.height);
                    player.setVelocityY(-50); // Mental ke bawah
                }
                // ===== COLLISION DARI KIRI (Player nabrak box dari kiri) =====
                else if (minOverlap == overlapLeft) {
                    player.setX(boxBounds.x - pBounds.width);
                    player.setVelocityX(0); // Stop gerakan horizontal
                }
                // ===== COLLISION DARI KANAN (Player nabrak box dari kanan) =====
                else if (minOverlap == overlapRight) {
                    player.setX(boxBounds.x + boxBounds.width);
                    player.setVelocityX(0); // Stop gerakan horizontal
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

                    if (keysCollected >= totalKeys) {
                        door.unlock();
                    }
                }
            }
        }
    }

    private void handleObstacleCollisions() {
        if (spawnProtectionTimer > 0) return;
        if (fireGirl.isDead() || waterBoy.isDead()) return;

        for (Obstacle obstacle : obstacles) {
            Rectangle fireGirlHitbox = new Rectangle(
                fireGirl.getBounds().x + 10,
                fireGirl.getBounds().y + 5,
                fireGirl.getBounds().width - 20,
                fireGirl.getBounds().height - 10
            );

            Rectangle waterBoyHitbox = new Rectangle(
                waterBoy.getBounds().x + 10,
                waterBoy.getBounds().y + 5,
                waterBoy.getBounds().width - 20,
                waterBoy.getBounds().height - 10
            );

            if (obstacle.getType() == Obstacle.ObstacleType.WATER &&
                fireGirlHitbox.overlaps(obstacle.getBounds())) {
                fireGirl.die();
                System.out.println("FireBoy touched WATER! Game Over!");
            }

            if (obstacle.getType() == Obstacle.ObstacleType.FIRE &&
                waterBoyHitbox.overlaps(obstacle.getBounds())) {
                waterBoy.die();
                System.out.println("WaterGirl touched LAVA! Game Over!");
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
