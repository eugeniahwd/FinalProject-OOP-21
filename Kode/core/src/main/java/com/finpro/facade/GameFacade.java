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

        System.out.println("FireGirl initial: (" + fireGirl.getX() + "," + fireGirl.getY() + ")");
        System.out.println("WaterBoy initial: (" + waterBoy.getX() + "," + waterBoy.getY() + ")");
    }

    private void setupEasyLevel() {
        // platform bawah
        platforms.add(EntityFactory.createPlatform(200, 250, 100, true));
        platforms.add(EntityFactory.createPlatform(200, 250, 200, false));
        platforms.add(EntityFactory.createPlatform(620, 190, 400, false));
        platforms.add(EntityFactory.createPlatform(1200, 190, 200, false));

        // platform tengah
        platforms.add(EntityFactory.createPlatform(70, 330, 150, false));
        platforms.add(EntityFactory.createPlatform(700, 400, 200, false));

        // platform atas
        platforms.add(EntityFactory.createPlatform(170, 500, 340, false));
        platforms.add(EntityFactory.createPlatform(330, 520, 220, true));
        platforms.add(EntityFactory.createPlatform(900, 570, 200, false));
        platforms.add(EntityFactory.createPlatform(500, 600, 200, false));

        // platform paling atas
        platforms.add(EntityFactory.createPlatform(700, 750, 200, false));

        // moving box
        boxes.add(EntityFactory.createMovingBox(300, 330, 250, 650));

        // obstacle ground
        obstacles.add(EntityFactory.createObstacle(550, 9, Obstacle.ObstacleType.FIRE));      // Far right on ground
        obstacles.add(EntityFactory.createObstacle(900, 9, Obstacle.ObstacleType.WATER));     // Even further

        // obstacle di platform bawah
        obstacles.add(EntityFactory.createObstacle(725, 169, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(210, 229, Obstacle.ObstacleType.WATER));

        obstacles.add(EntityFactory.createObstacle(444, 579, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(965, 549, Obstacle.ObstacleType.WATER));

        // diamonds di platform bawah
        diamondPool.obtain(630, 150, Diamond.DiamondType.BLUE);
        diamondPool.obtain(980, 150, Diamond.DiamondType.RED);

        // di platform tengah
        diamondPool.obtain(100, 430, Diamond.DiamondType.RED);      // Platform 100 (safe)
        diamondPool.obtain(805, 320, Diamond.DiamondType.BLUE);     // Platform 700 right

        // di platform atas
        diamondPool.obtain(620, 700, Diamond.DiamondType.BLUE);     // Platform 250 right
        diamondPool.obtain(940, 670, Diamond.DiamondType.RED);      // Platform 900

        // keys
        keys.add(EntityFactory.createKey(250, 575));
        keys.add(EntityFactory.createKey(1280, 270));

        door = EntityFactory.createDoor(770, 771);
    }

    private void setupMediumLevel() {
        // semua platform
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

        // moving box
        boxes.add(EntityFactory.createMovingBox(600, 130, 580, 750));
        boxes.add(EntityFactory.createVerticalMovingBox(1100, 330, 330, 480));
        boxes.add(EntityFactory.createVerticalMovingBox(290, 650, 650, 790));

        // obstacle di platform bawah
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

        // platform bawah
        diamondPool.obtain(650, 240, Diamond.DiamondType.BLUE);
        diamondPool.obtain(700, 240, Diamond.DiamondType.RED);

        // platform tengah
        diamondPool.obtain(260, 315, Diamond.DiamondType.BLUE);
        diamondPool.obtain(723, 425, Diamond.DiamondType.RED);

        // platform atas
        diamondPool.obtain(970, 760, Diamond.DiamondType.BLUE);
        diamondPool.obtain(10, 650, Diamond.DiamondType.RED);

        // keys
        keys.add(EntityFactory.createKey(445, 100));
        keys.add(EntityFactory.createKey(1240, 640));
        keys.add(EntityFactory.createKey(120, 660));

        door = EntityFactory.createDoor(670, 845);
    }

    private void setupHardLevel() {
        // platform di bawah 3
        platforms.add(EntityFactory.createPlatform(0, 150, 170, false));
        platforms.add(EntityFactory.createPlatform(320, 150, 150, false));
        platforms.add(EntityFactory.createPlatform(620, 150, 150, false));
        platforms.add(EntityFactory.createPlatform(920, 150, 150, false));
        // platform lift pojok
        platforms.add(EntityFactory.createPlatform(1500, 40, 185, true));
        // platform lift tengah
        platforms.add(EntityFactory.createPlatform(770, 400, 115, false));
        platforms.add(EntityFactory.createPlatform(620, 350, 330, true));
        // yang kecil sebelum turunan
        platforms.add(EntityFactory.createPlatform(520, 680, 120, false));
        // yang ada diamondnya
        platforms.add(EntityFactory.createPlatform(280, 500, 200, false));
        // yang ke kanan buat jalur ambik kuncih
        platforms.add(EntityFactory.createPlatform(770, 680, 380, false));

        //buat obstacle penyebrangan ke ujung di atas
        platforms.add(EntityFactory.createPlatform(1150, 500, 320, false));

        // buat platform paling atas
        platforms.add(EntityFactory.createPlatform(450, 780, 700, false));
        platforms.add(EntityFactory.createPlatform(450, 610, 170, true));
        platforms.add(EntityFactory.createPlatform(300, 610, 300, true));
        platforms.add(EntityFactory.createPlatform(300, 610, 150, false));

        // buat di pojok kanan atas ada diamond
        platforms.add(EntityFactory.createPlatform(1500, 780, 250, false));
        platforms.add(EntityFactory.createPlatform(1500, 680, 100, true));

        // platform buat pintu
        platforms.add(EntityFactory.createPlatform(0, 780, 70, false));

        boxes.add(EntityFactory.createVerticalMovingBox(1530, 50, 60, 500));
        boxes.add(EntityFactory.createMovingBox(1450, 300, 1000, 1480));
        boxes.add(EntityFactory.createVerticalMovingBox(695, 355, 370, 600));
        boxes.add(EntityFactory.createMovingBox(1250, 680, 1250, 1350));
        boxes.add(EntityFactory.createVerticalMovingBox(120, 350, 300, 650));

        obstacles.add(EntityFactory.createObstacle(150, 9, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(450, 9, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(750, 9, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1050, 9, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(-50, 130, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(30, 130, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(-50, 130, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(30, 130, Obstacle.ObstacleType.FIRE));

        //obstacle sebelum kunci
        obstacles.add(EntityFactory.createObstacle(1000, 760, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(880, 760, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(760, 760, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(640, 760, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(520, 760, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(520, 760, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(400, 760, Obstacle.ObstacleType.FIRE));

        //obstacle kunci
        obstacles.add(EntityFactory.createObstacle(285, 590, Obstacle.ObstacleType.WATER));

        // obstacle lift
        obstacles.add(EntityFactory.createObstacle(745, 380, Obstacle.ObstacleType.FIRE));

        // obstacle 1 biji di atas
        obstacles.add(EntityFactory.createObstacle(280, 480, Obstacle.ObstacleType.FIRE));

        // obstacle pojok kanan atas
        obstacles.add(EntityFactory.createObstacle(1460, 760, Obstacle.ObstacleType.WATER));

        //obstacle deret penyebrangan atas
        obstacles.add(EntityFactory.createObstacle(1095, 480, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1095, 480, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1175, 480, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1175, 480, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1255, 480, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(1255, 480, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1335, 480, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(1335, 480, Obstacle.ObstacleType.WATER));

        // obstacles deret bawah
        obstacles.add(EntityFactory.createObstacle(290, 130, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(315, 130, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(585, 130, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(610, 130, Obstacle.ObstacleType.FIRE));
        obstacles.add(EntityFactory.createObstacle(885, 130, Obstacle.ObstacleType.WATER));
        obstacles.add(EntityFactory.createObstacle(910, 130, Obstacle.ObstacleType.WATER));

        diamondPool.obtain(225, 150, Diamond.DiamondType.RED);
        diamondPool.obtain(525, 150, Diamond.DiamondType.BLUE);
        diamondPool.obtain(825, 150, Diamond.DiamondType.RED);
        diamondPool.obtain(1125, 150, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1550, 870, Diamond.DiamondType.RED);

        // diamond di platform deret bawah
        diamondPool.obtain(385, 230, Diamond.DiamondType.RED);
        diamondPool.obtain(685, 230, Diamond.DiamondType.BLUE);
        diamondPool.obtain(985, 230, Diamond.DiamondType.RED);

        // diamond di jalur tikus
        diamondPool.obtain(770, 720, Diamond.DiamondType.RED);
        diamondPool.obtain(830, 720, Diamond.DiamondType.BLUE);
        diamondPool.obtain(890, 720, Diamond.DiamondType.RED);
        diamondPool.obtain(950, 720, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1010, 720, Diamond.DiamondType.RED);
        diamondPool.obtain(1070, 720, Diamond.DiamondType.BLUE);
        diamondPool.obtain(1130, 720, Diamond.DiamondType.RED);

        //diamond sebiji
        diamondPool.obtain(360, 575, Diamond.DiamondType.BLUE);

        keys.add(EntityFactory.createKey(1535, 750));
        keys.add(EntityFactory.createKey(260, 870));
        keys.add(EntityFactory.createKey(375, 680));

        door = EntityFactory.createDoor(0, 800);
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

        handleCollisions();

        if (spawnProtectionTimer <= 0 && (fireGirl.isDead() || waterBoy.isDead())) {
            System.out.println("Player death detected!");
            gameOver = true;
        }
    }

    public void renderGame(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // background
        batch.begin();
        batch.draw(background, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        batch.end();
        // shape yang filled
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Platform platform : platforms) platform.render(shapeRenderer);
        for (Box box : boxes) box.render(shapeRenderer);

        door.render(shapeRenderer);
        door.renderHandle(shapeRenderer);

        for (Key key : keys) key.renderShape(shapeRenderer);

        shapeRenderer.end();
        // outline untuk shape yg line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Box box : boxes) box.renderOutline(shapeRenderer);
        door.renderLine(shapeRenderer);

        shapeRenderer.end();
        // sprite
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
                // collision pas landing
                if (player.getVelocityY() <= 0) {
                    float playerBottom = pBounds.y;
                    float platformTop = platBounds.y + platBounds.height;

                    if (playerBottom >= platformTop - 15 && playerBottom <= platformTop + 2) {
                        float overlap = platformTop - playerBottom;

                        if (Math.abs(overlap) < 20) {
                            player.setY(platformTop);
                            player.setOnGround(true);
                            onPlatform = true;
                            continue;
                        }
                    }
                }
                // collision kalo kepalanya nabrak
                else if (player.getVelocityY() > 0) {
                    float playerTop = pBounds.y + pBounds.height;
                    float platformBottom = platBounds.y;

                    if (playerTop >= platformBottom - 2 && playerTop <= platformBottom + 15) {
                        player.setVelocityY(-50);
                        continue;
                    }
                }
                // collision horizontal
                float playerCenterY = pBounds.y + pBounds.height / 2;
                float platformCenterY = platBounds.y + platBounds.height / 2;
                // player != landing dan atau nabrak ke atas
                if (Math.abs(playerCenterY - platformCenterY) < platBounds.height / 2) {
                    float playerRight = pBounds.x + pBounds.width;
                    float playerLeft = pBounds.x;
                    float platformRight = platBounds.x + platBounds.width;
                    float platformLeft = platBounds.x;
                    // collision kiri
                    if (playerRight > platformLeft && playerLeft < platformLeft) {
                        player.setX(platformLeft - pBounds.width);
                        player.setVelocityX(0);
                    }
                    // collision kanan
                    else if (playerLeft < platformRight && playerRight > platformRight) {
                        player.setX(platformRight);
                        player.setVelocityX(0);
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
                    if (box.getVelocityY() <= 0) { // collision vertical
                        float boxBottom = boxBounds.y;
                        float platformTop = platBounds.y + platBounds.height;

                        if (boxBottom >= platformTop - 15 && boxBottom <= platformTop + 2) {
                            box.setY(platformTop);
                            box.setOnGround(true);
                            onPlatform = true;
                            continue;
                        }
                    } else if (box.getVelocityY() > 0) {
                        float boxTop = boxBounds.y + boxBounds.height;
                        float platformBottom = platBounds.y;

                        if (boxTop >= platformBottom - 2 && boxTop <= platformBottom + 15) {
                            box.setVelocityY(-50);
                            continue;
                        }
                    }

                    // collision horizontal
                    float boxCenterY = boxBounds.y + boxBounds.height / 2;
                    float platformCenterY = platBounds.y + platBounds.height / 2;

                    if (Math.abs(boxCenterY - platformCenterY) < platBounds.height / 2) {
                        float boxRight = boxBounds.x + boxBounds.width;
                        float boxLeft = boxBounds.x;
                        float platformRight = platBounds.x + platBounds.width;
                        float platformLeft = platBounds.x;

                        // collision kiri
                        if (boxRight > platformLeft && boxLeft < platformLeft) {
                            box.setX(platformLeft - boxBounds.width);
                        }
                        // collision kanan
                        else if (boxLeft < platformRight && boxRight > platformRight) {
                            box.setX(platformRight);
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

                // land di box
                if (minOverlap == overlapBottom && player.getVelocityY() <= 0) {
                    player.setY(boxBounds.y + boxBounds.height);
                    player.setOnGround(true);

                    if (box.isMovingPlatform()) {
                        float deltaTime = com.badlogic.gdx.Gdx.graphics.getDeltaTime();

                        if (box.isVertical()) {
                            // ikut gerakan vetical
                            float deltaY = box.getVelocityY() * deltaTime;
                            player.setY(player.getY() + deltaY);
                        } else {
                            // ikut gerakan horizontal
                            float deltaX = box.getVelocityX() * deltaTime;
                            player.setX(player.getX() + deltaX);
                        }
                    }

                    foundBoxToStandOn = true;
                }
                // kepala nabrak
                else if (minOverlap == overlapTop && player.getVelocityY() > 0) {
                    player.setY(boxBounds.y - pBounds.height);
                    player.setVelocityY(-50);
                }
                // collision horizontal
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
