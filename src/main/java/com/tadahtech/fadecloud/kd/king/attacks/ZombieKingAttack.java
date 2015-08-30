package com.tadahtech.fadecloud.kd.king.attacks;

import com.tadahtech.fadecloud.kd.king.King;
import com.tadahtech.fadecloud.kd.king.KingAttack;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.util.Vector;

/**
 * Created by Timothy Andis
 */
public class ZombieKingAttack implements KingAttack {

    private int hits;

    @Override
    public void attack(Player player, King king) {
        LivingEntity livingEntity = (LivingEntity) king.getEntity().getBukkitEntity();
        Location kingLoc = livingEntity.getEyeLocation();
        Location playerLoc = player.getLocation();
        Vector direction = kingLoc.toVector().subtract(playerLoc.toVector()).normalize();
        WitherSkull skull = kingLoc.getWorld().spawn(kingLoc.add(0, 0.2, 0), WitherSkull.class);
        skull.setDirection(direction);
        skull.setBounce(false);
        skull.setIsIncendiary(false);
        skull.setCharged(true);
    }

    @Override
    public String getName() {
        return "Detach Head";
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
