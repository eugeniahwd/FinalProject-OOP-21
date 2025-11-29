package com.finpro.states;

import com.finpro.entities.Player;

/**
 * Dead State - Player cannot move when dead
 */
public class DeadState implements PlayerState {
    @Override
    public void update(Player player, float delta) {
        // Player is dead, cannot move
        // Could add death animation here
    }

    @Override
    public String getStateName() {
        return "DEAD";
    }
}
