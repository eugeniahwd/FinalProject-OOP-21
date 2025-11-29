package com.finpro.states;

import com.finpro.entities.Player;

/**
 * STATE PATTERN - Interface untuk berbagai state player
 */
public interface PlayerState {
    void update(Player player, float delta);
    String getStateName();
}
