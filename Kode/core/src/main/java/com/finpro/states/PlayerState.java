package com.finpro.states;

import com.finpro.entities.Player;

public interface PlayerState {
    void update(Player player, float delta);
    String getStateName();
}
