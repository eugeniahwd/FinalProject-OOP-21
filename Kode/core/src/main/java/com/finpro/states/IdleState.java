package com.finpro.states;

import com.finpro.entities.Player;

// Idle State
public class IdleState implements PlayerState {
    @Override
    public void update(Player player, float delta) {
        // Player tidak bergerak
    }

    @Override
    public String getStateName() {
        return "IDLE";
    }
}
