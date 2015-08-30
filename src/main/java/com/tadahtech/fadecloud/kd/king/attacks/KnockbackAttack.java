package com.tadahtech.fadecloud.kd.king.attacks;

import com.tadahtech.fadecloud.kd.king.King;
import com.tadahtech.fadecloud.kd.king.KingAttack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Created by Timothy Andis
 */
public class KnockbackAttack implements KingAttack {

    private int hits;

    @Override
    public void attack(Player player, King king) {
        Location kingLoc = king.getEntity().getBukkitEntity().getLocation();
        Location playerLoc = player.getLocation();
        Vector back = playerLoc.toVector().subtract(kingLoc.toVector()).normalize();
        player.setVelocity(back.multiply(1.5));
        player.damage(3.0);
    }

    @Override
    public String getName() {
        return "Knockback";
    }

    @Override
    public double getChance() {
        return 25;
    }

    @Override
    public void resetHits() {
        hits = 0;
    }

    @Override
    public int hitsTillAttack() {
        return 7;
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
