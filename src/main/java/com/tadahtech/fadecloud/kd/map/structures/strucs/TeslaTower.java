package com.tadahtech.fadecloud.kd.map.structures.strucs;

import com.sk89q.worldedit.Vector;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.StructureType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class TeslaTower extends DefenseStructure {

    public TeslaTower(PlayerInfo info) {
        super(ChatColor.AQUA.toString() + ChatColor.BOLD + "Tesla");
        this.setOwner(info);
        info.setCurrentStructure(this);
    }

    @Override
    public void launchProjectile(Location source, Vector direction) {
        //nope
    }

    @Override
    public void targetPlayer(Player player) {
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.damage(damage);
    }

    @Override
    public void give(Player player) {
        player.getInventory().addItem(DefenseStructure.TESLA);
    }

    @Override
    public StructureType getStructureType() {
        return StructureType.TESLA;
    }
}
