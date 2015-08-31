package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.map.structures.StructureRegion;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.teams.enderman.EndermanTeam;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if(info.isInvincible()) {
            event.setCancelled(true);
            return;
        }
        if(!(damagerEntity instanceof Player)) {
            if(damagerEntity instanceof Projectile) {
                Projectile projectile = (Projectile) damagerEntity;
                if(projectile.getShooter() == null || !(projectile.getShooter() instanceof Player)) {
                    info.getCurrentTeam().onOtherDamage(event, info);
                    return;
                }
                Player shooter = (Player) projectile.getShooter();
                PlayerInfo shooterInfo = KingdomDefense.getInstance().getInfoManager().get(shooter);
                if(shooterInfo.getCurrentTeam().equals(info.getCurrentTeam())) {
                    event.setCancelled(true);
                    return;
                }
            }
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
        if(info != null) {
            info.getCurrentTeam().onHit(event, info);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(KingdomDefense.EDIT_MODE) {
            return;
        }
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        if(to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if(info.getCurrentTeam() != null) {
            Optional<Structure> maybe = StructureRegion.getStructure(to);
            if(maybe.isPresent()) {
                Structure structure = maybe.get();
                int level = structure.getLevel();
                String message = structure.getName() + " " + structure.getLevel();
                if(level <= 0) {
                    message = ChatColor.AQUA + "Inactive.";
                }
                message = message  + ChatColor.GOLD + " -> Shift-Left-Click to edit";
                PacketUtil.sendActionBarMessage(player, message);
            }
            if(info.getCurrentTeam().getType() == TeamType.ENDERMAN) {
                EndermanTeam team = (EndermanTeam) info.getCurrentTeam();
                if(info.isInvisibleFromChance() || info.isInvisible()) {
                    team.onMove(info);
                }
            }
        }
    }
}
