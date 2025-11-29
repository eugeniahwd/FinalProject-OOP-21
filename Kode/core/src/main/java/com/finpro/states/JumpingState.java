package com.finpro.states;

import com.finpro.entities.Player;

// Jumping State
public class JumpingState implements PlayerState {
    @Override
    public void update(Player player, float delta) {
        // Efek saat melompat
        if (player.getVelocityY() <= 0) {
            player.setState(new IdleState());
        }
    }

    @Override
    public String getStateName() {
        return "JUMPING";
    }
}
