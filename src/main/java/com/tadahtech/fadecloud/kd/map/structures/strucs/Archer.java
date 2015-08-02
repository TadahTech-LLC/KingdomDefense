package com.tadahtech.fadecloud.kd.map.structures.strucs;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.StructureType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class Archer extends DefenseStructure {

    public Archer(PlayerInfo info) {
        super(ChatColor.GRAY.toString() + ChatColor.BOLD + "Archer Tower");
        this.setOwner(info);
        info.setCurrentStructure(this);
    }

    @Override
    public void launchProjectile(Location source, Vector direction) {
        Arrow arrow = source.getWorld().spawn(source, Arrow.class);
        arrow.setVelocity(new org.bukkit.util.Vector(direction.getX(), direction.getY(), direction.getZ()).multiply(3.5));
        arrow.setMetadata("damage", new FixedMetadataValue(KingdomDefense.getInstance(), damage));
    }


    @Override
    public void give(Player player) {
        player.getInventory().addItem(DefenseStructure.ARCHER);
    }

    @Override
    public StructureType getStructureType() {
        return StructureType.ARCHER;
    }
}
