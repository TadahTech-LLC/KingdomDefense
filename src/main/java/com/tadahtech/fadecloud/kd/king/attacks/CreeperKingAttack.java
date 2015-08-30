package com.tadahtech.fadecloud.kd.king.attacks;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.king.King;
import com.tadahtech.fadecloud.kd.king.KingAttack;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * Created by Timothy Andis
 */
public class CreeperKingAttack implements KingAttack {

    private int hits;

    @Override
    public void attack(Player player, King king) {
        LivingEntity livingEntity = (LivingEntity) king.getEntity().getBukkitEntity();
        Location kingLoc = livingEntity.getLocation();
        List<Location> circle = Utils.circle(kingLoc, 3, 0, true, false, 0);
        circle.forEach(location -> {
            location.getWorld().strikeLightningEffect(location);
            TNTPrimed tntPrimed = location.getWorld().spawn(location, TNTPrimed.class);
            tntPrimed.setFuseTicks(5);
            tntPrimed.setMetadata("creeper", new FixedMetadataValue(KingdomDefense.getInstance(), ""));
        });
    }

    @Override
    public String getName() {
        return "Explosive Circle";
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
