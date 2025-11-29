package com.finpro.pools;

import com.finpro.entities.Diamond;
import java.util.ArrayList;
import java.util.List;

/**
 * OBJECT POOL PATTERN
 * Menggunakan pool untuk recycle diamond objects
 * Menghindari garbage collection yang berlebihan
 */
public class DiamondPool {
    private List<Diamond> available;
    private List<Diamond> inUse;
    private static final int INITIAL_CAPACITY = 20;

    public DiamondPool() {
        available = new ArrayList<>();
        inUse = new ArrayList<>();

        // Pre-create diamonds
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            available.add(new Diamond(0, 0, Diamond.DiamondType.RED));
        }
    }

    public Diamond obtain(float x, float y, Diamond.DiamondType type) {
        Diamond diamond;

        if (available.isEmpty()) {
            // Create new if pool is empty
            diamond = new Diamond(x, y, type);
        } else {
            // Reuse from pool
            diamond = available.remove(available.size() - 1);
            diamond.reset(x, y, type);
        }

        inUse.add(diamond);
        return diamond;
    }

    public void free(Diamond diamond) {
        if (inUse.remove(diamond)) {
            available.add(diamond);
        }
    }

    public void freeAll() {
        available.addAll(inUse);
        inUse.clear();
    }

    public List<Diamond> getInUse() {
        return inUse;
    }

    public void dispose() {
        // Dispose all diamonds
        for (Diamond diamond : available) {
            diamond.dispose();
        }
        for (Diamond diamond : inUse) {
            diamond.dispose();
        }
        available.clear();
        inUse.clear();
    }
}
