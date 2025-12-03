package com.finpro.states;

import com.finpro.entities.Player;

public class JumpingState implements PlayerState {
    @Override
    public void update(Player player, float delta) {
        // FIXED: Only transition to idle when falling and near ground
        // Don't automatically transition - let physics handle it
    }

    @Override
    public String getStateName() {
        return "JUMPING";
    }
}
