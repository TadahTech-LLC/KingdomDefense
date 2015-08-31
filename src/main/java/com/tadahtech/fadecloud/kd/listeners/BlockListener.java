package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.creation.StructureRegionCreator;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.map.structures.StructureRegion;
import com.tadahtech.fadecloud.kd.menu.menus.StructureMenu;
import com.tadahtech.fadecloud.kd.menu.menus.UpgradeStructureMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class BlockListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (KingdomDefense.EDIT_MODE) {
            return;
        }
        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if (info == null) {
            return;
        }
        Optional<Structure> maybe = StructureRegion.getStructure(location);
        if (!maybe.isPresent()) {
            return;
        }
        event.setCancelled(true);
        info.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't break structures!");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Optional<StructureRegionCreator> creator = StructureRegionCreator.get(event.getPlayer());
        if(creator.isPresent()) {
            event.setCancelled(true);
            creator.get().onClick(event.getBlockPlaced().getLocation());
            return;
        }
        if (KingdomDefense.EDIT_MODE) {
            return;
        }

        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Optional<Structure> maybe = StructureRegion.getStructure(location);
        if (!maybe.isPresent()) {
            return;
        }
        event.setCancelled(true);
        info.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't place that here!");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (KingdomDefense.EDIT_MODE) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
        if (!player.isSneaking()) {
            return;
        }
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Location location = player.getLocation();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if (info.getCurrentTeam() == null) {
            return;
        }
        Optional<Structure> maybe = StructureRegion.getStructure(location);
        if (maybe.isPresent()) {
            event.setCancelled(true);
            Structure structure = maybe.get();
            if(structure.isActive()) {
                new UpgradeStructureMenu(structure).open(player);
            } else {
                new StructureMenu(structure).open(player);
            }
        }
    }

}
