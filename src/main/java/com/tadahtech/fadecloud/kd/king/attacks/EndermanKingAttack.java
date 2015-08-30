package com.tadahtech.fadecloud.kd.king.attacks;

import com.tadahtech.fadecloud.kd.king.King;
import com.tadahtech.fadecloud.kd.king.KingAttack;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class EndermanKingAttack implements KingAttack {

    private int hits;

    @Override
    public void attack(Player player, King king) {

    }

    @Override
    public String getName() {
        return "Enderman Blah";
    }

    @Override
    public double getChance() {
        return 40.0;
    }

    @Override
    public void resetHits() {
        hits = 0;
    }

    @Override
    public int hitsTillAttack() {
        return 10;
    }

    @Override
    public void hit() {
        hits++;
    }

    @Override
    public int getHits() {
        return hits;
    }

}
