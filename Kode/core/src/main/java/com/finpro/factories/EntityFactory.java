package com.finpro.factories;

import com.finpro.entities.*;

/**
 * FACTORY METHOD PATTERN
 */
public class EntityFactory {

    public static Player createFireGirl(float x, float y) {
        return new Player(x, y, Player.PlayerType.FIREGIRL);
    }

    public static Player createWaterBoy(float x, float y) {
        return new Player(x, y, Player.PlayerType.WATERBOY);
    }

    public static Diamond createDiamond(float x, float y, Diamond.DiamondType type) {
        return new Diamond(x, y, type);
    }

    public static Obstacle createObstacle(float x, float y, Obstacle.ObstacleType type) {
        return new Obstacle(x, y, type);
    }

    public static Box createBox(float x, float y) {
        return new Box(x, y);
    }

    // BARU: Moving platform box
    public static Box createMovingBox(float x, float y, float startX, float endX) {
        return new Box(x, y, startX, endX);
    }

    public static Door createDoor(float x, float y) {
        return new Door(x, y);
    }

    public static Platform createPlatform(float x, float y, float width, boolean isVertical) {
        return new Platform(x, y, width, isVertical);
    }

    public static Key createKey(float x, float y) {
        return new Key(x, y);
    }
}
