package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.structures.GridLocation;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.map.structures.WorldEditAssit;
import com.tadahtech.fadecloud.kd.map.structures.strucs.Guardian;
import com.tadahtech.fadecloud.kd.menu.menus.UpgradeStructureMenu;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class BlockListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if(KingdomDefense.EDIT_MODE) {
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
        Island island = info.getCurrentTeam().getIsland();
        GridLocation gridLocation = GridLocation.fromWorldLocation(island, location);
        Optional<Structure> maybe = info.getCurrentTeam().getIsland().getStructure(gridLocation);
        if (!maybe.isPresent()) {
            if (island.inCastle(location)) {
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        info.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't break structures!");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if(KingdomDefense.EDIT_MODE) {
            return;
        }
        ItemStack inhand = event.getItemInHand();
        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Island island = info.getCurrentTeam().getIsland();
        if (!island.getRegion().canBuild(location)) {
            return;
        }
        if (island.inCastle(location)) {
            event.setCancelled(true);
            info.sendMessage(ChatColor.RED + "You cannot place in your castle!");
            return;
        }
        if(!island.getRegion().canBuild(location)) {
            if(KingdomDefense.getInstance().getGame().getState() != GameState.BATTLE) {
                info.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't place that here!");
                event.setCancelled(true);
                return;
            }
        }
        GridLocation gridLocation = GridLocation.fromWorldLocation(island, location);
        Optional<Structure> maybe = info.getCurrentTeam().getIsland().getStructure(gridLocation);
        if (!maybe.isPresent()) {
            if (inhand == null || inhand.getType() == Material.AIR) {
                return;
            }
            Structure structure = info.getCurrentStructure();
            if (structure == null) {
                return;
            }
            if(!WorldEditAssit.structureCheck(island, gridLocation, structure, 1)) {
                info.sendMessage(ChatColor.RED + "You have something in the way!");
                return;
            }
            if(!WorldEditAssit.pasteStructure(island, gridLocation, structure, 0, 1, true)) {
                return;
            }
            info.setCurrentStructure(null);
            structure.setLevel(1);
            if(structure instanceof Guardian) {
                Guardian guardian = (Guardian) structure;
                guardian.setHealthPerTick(2);
                guardian.setCooldown(20);
            }
            event.setCancelled(true);
            info.sendMessage(ModSpecialItem.PREFIX + structure.getName() + ChatColor.YELLOW + " created! " + (4 - island.getCount(structure.getStructureType())) + " more can be made.");
            return;
        }
        event.setCancelled(true);
        info.getBukkitPlayer().sendMessage(ChatColor.RED + "You can't place that here!");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(KingdomDefense.EDIT_MODE) {
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
        Island island = info.getCurrentTeam().getIsland();
        GridLocation gridLocation = GridLocation.fromWorldLocation(island, location);
        Optional<Structure> maybe = info.getCurrentTeam().getIsland().getStructure(gridLocation);
        if (maybe.isPresent()) {
            Structure structure = maybe.get();
            new UpgradeStructureMenu(structure).open(player);
        }
    }
}
