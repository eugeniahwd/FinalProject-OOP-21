package com.finpro.states;

import com.finpro.entities.Player;

public class DeadState implements PlayerState {
    @Override
    public void update(Player player, float delta) {
    }

    @Override
    public String getStateName() { return "DEAD"; }
}
