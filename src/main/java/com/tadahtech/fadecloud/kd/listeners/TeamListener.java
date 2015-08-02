package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.teams.enderman.EndermanTeam;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

/**
 * Created by Timothy Andis (TadahTech) on 7/25/2015.
 */
public class TeamListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damagerEntity = event.getDamager();
        if(!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if(!(damagerEntity instanceof Player)) {
           info.getCurrentTeam().onOtherDamage(event, info);
            return;
        }
        Player damager = (Player) damagerEntity;
        PlayerInfo damagerInfo = KingdomDefense.getInstance().getInfoManager().get(damager);
        if(damagerInfo.getCurrentTeam().equals(info.getCurrentTeam())) {
            event.setCancelled(true);
            return;
        }
        damagerInfo.getCurrentTeam().onDamage(event, damagerInfo);
        info.getCurrentTeam().onHit(event, info);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if(to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if(info.getCurrentTeam() != null) {
            Island island = info.getCurrentTeam().getIsland();
            Optional<Structure> maybe = island.getStructure(to);
            if(maybe.isPresent()) {
                Structure structure = maybe.get();
                String message = structure.getName() + " " + structure.getLevel() + ChatColor.DARK_GRAY + " -> Shift-Left-Click to edit";
                PacketUtil.sendActionBarMessage(player, message);
            }
        }
        Game game = KingdomDefense.getInstance().getGame();
        game.moveCheck(player, to);
        if(info.getCurrentTeam().getType() != TeamType.ENDERMAN) {
            return;
        }
        EndermanTeam team = (EndermanTeam) info.getCurrentTeam();
        team.onMove(info);
    }
}
