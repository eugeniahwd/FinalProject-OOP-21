package com.finpro.states;

import com.finpro.entities.Player;

// Moving State
public class MovingState implements PlayerState {
    @Override
    public void update(Player player, float delta) {
        // Animation atau effect saat bergerak bisa ditambahkan di sini
    }

    @Override
    public String getStateName() {
        return "MOVING";
    }
}
