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
    private int fireGirlRedDiamonds = 0;
    private int waterBoyBlueDiamonds = 0;
    private int fireGirlScore = 0;
    private int waterBoyScore = 0;
    private float gameTime = 0;
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

        background = new Texture("walls.png");
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

        // Debug player initial positions
        System.out.println("FireGirl initial: (" + fireGirl.getX() + "," + fireGirl.getY() + ")");
        System.out.println("WaterBoy initial: (" + waterBoy.getX() + "," + waterBoy.getY() + ")");
    }

    private void setupEasyLevel() {
        // Lower platforms
        platforms.add(EntityFactory.createPlatform(200, 250, 100, true));
        platforms.add(EntityFactory.createPlatform(200, 250, 200, false));
        platforms.add(EntityFactory.createPlatform(620, 190, 400, false));
        platforms.add(EntityFactory.createPlatform(1200, 190, 200, false));

        // Middle platforms
        platforms.add(EntityFactory.createPlatform(70, 330, 150, false));
        platforms.add(EntityFactory.createPlatform(700, 400, 200, false));

        // Upper platforms
        platforms.add(EntityFactory.createPlatform(170, 500, 340, false));
        platforms.add(EntityFactory.createPlatform(330, 520, 220, true));
        platforms.add(EntityFactory.createPlatform(900, 570, 200, false));
        platforms.add(EntityFactory.createPlatform(500, 600, 200, false));

        // Top platform
        platforms.add(EntityFactory.createPlatform(700, 750, 200, false));

        // MOVING BOXES
        boxes.add(EntityFactory.createMovingBox(300, 330, 250, 650));

        // OBSTACLES FIRST - Placed strategically
        // Ground level - spread out
        obstacles.add(EntityFactory.createObstacle(550, 9, Obstacle.ObstacleType.FIRE));      // Far right on ground
        obstacles.add(EntityFactory.createObstacle(900, 9, Obstacle.ObstacleType.WATER));     // Even further

        // Lower platforms - one per platform, far from edges
        obstacles.add(EntityFactory.createObstacle(725, 169, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(210, 229, Obstacle.ObstacleType.WATER));

        obstacles.add(EntityFactory.createObstacle(444, 579, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(965, 549, Obstacle.ObstacleType.WATER));


        // DIAMONDS - Safe positions, away from obstacles
        // Lower platforms - safe spots
        diamondPool.obtain(630, 150, Diamond.DiamondType.BLUE);
        diamondPool.obtain(980, 150, Diamond.DiamondType.RED);

        // Middle platforms - safe spots
        diamondPool.obtain(100, 430, Diamond.DiamondType.RED);      // Platform 100 (safe)
        diamondPool.obtain(805, 320, Diamond.DiamondType.BLUE);     // Platform 700 right

        // Upper platforms - all safe
        diamondPool.obtain(620, 700, Diamond.DiamondType.BLUE);     // Platform 250 right
        diamondPool.obtain(940, 670, Diamond.DiamondType.RED);      // Platform 900

        // Keys - safe positions
        keys.add(EntityFactory.createKey(250, 575));
        keys.add(EntityFactory.createKey(1280, 270));

        door = EntityFactory.createDoor(770, 771);
    }

    private void setupMediumLevel() {
        // Platform layers
        platforms.add(EntityFactory.createPlatform(300, 130, 200, false));
        platforms.add(EntityFactory.createPlatform(500, 50, 100, true));
        platforms.add(EntityFactory.createPlatform(850, 130, 500, false));
        platforms.add(EntityFactory.createPlatform(850, 50, 100, true));

        platforms.add(EntityFactory.createPlatform(200, 350, 150, false));
        platforms.add(EntityFactory.createPlatform(700, 350, 150, false));
        platforms.add(EntityFactory.createPlatform(965, 280, 100, false));

        platforms.add(EntityFactory.createPlatform(200, 250, 100, true));
        platforms.add(EntityFactory.createPlatform(200, 250, 200, false));

        platforms.add(EntityFactory.createPlatform(1175, 550, 150, false));
        platforms.add(EntityFactory.createPlatform(390, 570, 130, false));
        platforms.add(EntityFactory.createPlatform(500, 500, 80, true));
        platforms.add(EntityFactory.createPlatform(500, 480, 150, false));

        platforms.add(EntityFactory.createPlatform(0, 600, 180, false));
        platforms.add(EntityFactory.createPlatform(50, 600, 90, true));

        platforms.add(EntityFactory.createPlatform(925, 710, 130, false));

        platforms.add(EntityFactory.createPlatform(500, 820, 380, false));

        // MOVING BOXES
        boxes.add(EntityFactory.createMovingBox(600, 130, 580, 750));
        boxes.add(EntityFactory.createVerticalMovingBox(1100, 330, 330, 480));
        boxes.add(EntityFactory.createVerticalMovingBox(290, 650, 650, 790));

        // OBSTACLES FIRST - Placed strategically
        // Ground level - spread out
        obstacles.add(EntityFactory.createObstacle(360, 9, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(465, 9, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(550, 9, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(635, 9, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(720, 9, Obstacle.ObstacleType.WATER));

        obstacles.add(EntityFactory.createObstacle(180, 230, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(643, 330, Obstacle.ObstacleType.FIRE));

        obstacles.add(EntityFactory.createObstacle(1153, 530, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(35, 580, Obstacle.ObstacleType.FIRE));

        obstacles.add(EntityFactory.createObstacle(500, 800, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(700, 800, Obstacle.ObstacleType.FIRE));

        // DIAMONDS
        // Lower platforms
        diamondPool.obtain(650, 240, Diamond.DiamondType.BLUE);
        diamondPool.obtain(700, 240, Diamond.DiamondType.RED);

        // Middle platforms
        diamondPool.obtain(260, 315, Diamond.DiamondType.BLUE);
        diamondPool.obtain(723, 425, Diamond.DiamondType.RED);

        // Upper platforms
        diamondPool.obtain(970, 760, Diamond.DiamondType.BLUE);
        diamondPool.obtain(10, 650, Diamond.DiamondType.RED);

        // Keys
        keys.add(EntityFactory.createKey(445, 100));
        keys.add(EntityFactory.createKey(1240, 640));
        keys.add(EntityFactory.createKey(120, 660));


        door = EntityFactory.createDoor(670, 845);
    }

    private void setupHardLevel() {
        platforms.add(EntityFactory.createPlatform(0, 0, 200, false));
        platforms.add(EntityFactory.createPlatform(350, 0, 150, false));
        platforms.add(EntityFactory.createPlatform(650, 0, 150, false));
        platforms.add(EntityFactory.createPlatform(950, 0, 150, false));
        platforms.add(EntityFactory.createPlatform(1250, 0, 350, false));

        platforms.add(EntityFactory.createPlatform(0, 780, 200, false));
        platforms.add(EntityFactory.createPlatform(200, 350, 430, true));

        platforms.add(EntityFactory.createPlatform(200, 350, 350, false));

        platforms.add(EntityFactory.createPlatform(550, 450, 250, true));
        platforms.add(EntityFactory.createPlatform(550, 450, 200, false));
        platforms.add(EntityFactory.createPlatform(750, 450, 250, true));
        platforms.add(EntityFactory.createPlatform(550, 700, 800, false));

        platforms.add(EntityFactory.createPlatform(1000, 400, 250, false));
        platforms.add(EntityFactory.createPlatform(1400, 650, 200, false));

        boxes.add(EntityFactory.createVerticalMovingBox(100, 100, 100, 350));
        boxes.add(EntityFactory.createVerticalMovingBox(100, 350, 350, 780));
        boxes.add(EntityFactory.createVerticalMovingBox(620, 200, 200, 450));
        boxes.add(EntityFactory.createVerticalMovingBox(1500, 100, 100, 650));

        boxes.add(EntityFactory.createMovingBox(1100, 350, 800, 1150));

        obstacles.add(EntityFactory.createObstacle(200, 0, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(260, 0, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(500, 0, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(560, 0, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(800, 0, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(860, 0, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1100, 0, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1160, 0, Obstacle.ObstacleType.WATER));

        obstacles.add(EntityFactory.createObstacle(300, 350, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(350, 350, Obstacle.ObstacleType.WATER));

        obstacles.add(EntityFactory.createObstacle(600, 450, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(660, 450, Obstacle.ObstacleType.FIRE));

        obstacles.add(EntityFactory.createObstacle(1050, 400, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1110, 400, Obstacle.ObstacleType.FIRE));

        obstacles.add(EntityFactory.createObstacle(900, 700, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(960, 700, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1150, 700, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1210, 700, Obstacle.ObstacleType.WATER));

        diamondPool.obtain(330, 480, Diamond.DiamondType.RED);
        diamondPool.obtain(230, 100, Diamond.DiamondType.RED);
        diamondPool.obtain(530, 100, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1080, 550, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1130, 100, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1450, 750, Diamond.DiamondType.RED);

        keys.add(EntityFactory.createKey(80, 850));
        keys.add(EntityFactory.createKey(630, 600));
        keys.add(EntityFactory.createKey(1500, 100));

        door = EntityFactory.createDoor(50, 780);
    }

    public void updateGame(float delta) {
        if (gameOver) return;

        gameTime += delta;

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

        handleCollisions();  // Di sini movement box akan diterapkan

        if (spawnProtectionTimer <= 0 && (fireGirl.isDead() || waterBoy.isDead())) {
            System.out.println("Player death detected!");
            gameOver = true;
        }
    }

    public void renderGame(SpriteBatch batch, ShapeRenderer shapeRenderer) {

        // BACKGROUND (sprite)
        batch.begin();
        batch.draw(background, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        batch.end();

        // ===== FILLED SHAPES =====
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Platform platform : platforms) platform.render(shapeRenderer);
        for (Box box : boxes) box.render(shapeRenderer);

        door.render(shapeRenderer);
        door.renderHandle(shapeRenderer);

        for (Key key : keys) key.renderShape(shapeRenderer);

        shapeRenderer.end();

        // ===== LINE SHAPES (outlines) =====
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Box box : boxes) box.renderOutline(shapeRenderer);
        door.renderLine(shapeRenderer);

        shapeRenderer.end();

        // ===== SPRITES =====
        batch.begin();

        for (Obstacle obstacle : obstacles) obstacle.render(batch);
        for (Diamond diamond : diamondPool.getInUse()) diamond.render(batch);
        for (Key key : keys) key.render(batch);

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
        boolean foundBoxToStandOn = false;

        for (Box box : boxes) {
            Rectangle boxBounds = box.getBounds();

            if (pBounds.overlaps(boxBounds)) {
                float overlapLeft = (pBounds.x + pBounds.width) - boxBounds.x;
                float overlapRight = (boxBounds.x + boxBounds.width) - pBounds.x;
                float overlapTop = (pBounds.y + pBounds.height) - boxBounds.y;
                float overlapBottom = (boxBounds.y + boxBounds.height) - pBounds.y;

                float minOverlap = Math.min(
                    Math.min(overlapLeft, overlapRight),
                    Math.min(overlapTop, overlapBottom)
                );

                // Landing di box
                if (minOverlap == overlapBottom && player.getVelocityY() <= 0) {
                    player.setY(boxBounds.y + boxBounds.height);
                    player.setOnGround(true);

                    // LANGSUNG terapkan movement box ke player
                    if (box.isMovingPlatform()) {
                        float deltaTime = com.badlogic.gdx.Gdx.graphics.getDeltaTime();

                        if (box.isVertical()) {
                            // Ikut gerakan VERTICAL
                            float deltaY = box.getVelocityY() * deltaTime;
                            player.setY(player.getY() + deltaY);
                        } else {
                            // Ikut gerakan HORIZONTAL
                            float deltaX = box.getVelocityX() * deltaTime;
                            player.setX(player.getX() + deltaX);
                        }
                    }

                    foundBoxToStandOn = true;
                }
                // Head bump
                else if (minOverlap == overlapTop && player.getVelocityY() > 0) {
                    player.setY(boxBounds.y - pBounds.height);
                    player.setVelocityY(-50);
                }
                // Collision horizontal
                else if (minOverlap == overlapLeft) {
                    player.setX(boxBounds.x - pBounds.width);
                    player.setVelocityX(0);
                }
                else if (minOverlap == overlapRight) {
                    player.setX(boxBounds.x + boxBounds.width);
                    player.setVelocityX(0);
                }
            }
        }
    }


    // Method baru: Update posisi player berdasarkan box yang dinaiki
    private void updatePlayerOnMovingBox(Player player, float delta) {
        Box box = player.getStandingOnBox();

        if (box != null && box.isMovingPlatform()) {
            if (box.isVertical()) {
                // Ikut gerakan VERTICAL
                float deltaY = box.getVelocityY() * delta;
                player.setY(player.getY() + deltaY);
            } else {
                // Ikut gerakan HORIZONTAL
                float deltaX = box.getVelocityX() * delta;
                player.setX(player.getX() + deltaX);
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

                    // TAMBAHAN: Track diamonds dan score
                    fireGirlRedDiamonds++;
                    fireGirlScore += 100;
                    System.out.println("FireGirl: " + fireGirlRedDiamonds + " red diamonds, Score: " + fireGirlScore);

                } else if (diamond.getType() == Diamond.DiamondType.BLUE &&
                    expandedWaterBoy.overlaps(diamond.getBounds())) {
                    diamond.collect();

                    // TAMBAHAN: Track diamonds dan score
                    waterBoyBlueDiamonds++;
                    waterBoyScore += 100;
                    System.out.println("WaterBoy: " + waterBoyBlueDiamonds + " blue diamonds, Score: " + waterBoyScore);
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

        boolean bothAtDoor = fireGirl.getBounds().overlaps(door.getBounds()) &&
            waterBoy.getBounds().overlaps(door.getBounds());

        return bothAtDoor;
    }

    public Player getFireGirl() { return fireGirl; }
    public Player getWaterBoy() { return waterBoy; }
    public boolean isGameOver() { return gameOver; }
    public int getKeysCollected() { return keysCollected; }
    public int getTotalKeys() { return totalKeys; }
    public boolean isDoorUnlocked() { return !door.isLocked(); }
    public int getFireGirlRedDiamonds() {return fireGirlRedDiamonds;}
    public int getWaterBoyBlueDiamonds() {return waterBoyBlueDiamonds;}
    public int getFireGirlScore() {return fireGirlScore;}
    public int getWaterBoyScore() {return waterBoyScore;}
    public int getGameTimeSeconds() {return (int) gameTime;}

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
