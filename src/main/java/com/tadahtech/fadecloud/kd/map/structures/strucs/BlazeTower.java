package com.tadahtech.fadecloud.kd.map.structures.strucs;

import com.sk89q.worldedit.Vector;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.StructureType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class BlazeTower extends DefenseStructure {

    public BlazeTower(PlayerInfo info) {
        super(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Blaze");
        this.setOwner(info);
        info.setCurrentStructure(this);
    }

    @Override
    public void launchProjectile(Location source, Vector direction) {
        Fireball fireball = source.getWorld().spawn(source, Fireball.class);
        fireball.setVelocity(new org.bukkit.util.Vector(direction.getX(), direction.getY(), direction.getZ()).multiply(3.5));
        fireball.setBounce(false);
        fireball.setIsIncendiary(false);
        fireball.setMetadata("damage", new FixedMetadataValue(KingdomDefense.getInstance(), damage));
    }

    @Override
    public void give(Player player) {
        player.getInventory().addItem(DefenseStructure.BLAZE);
    }

    @Override
    public StructureType getStructureType() {
        return StructureType.BLAZE;
    }
}
