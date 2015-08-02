package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.menu.menus.PurchaseStructureMenu;
import com.tadahtech.fadecloud.kd.nms.King;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Created by Timothy Andis (TadahTech) on 8/1/2015.
 */
public class EntityListener implements Listener {

    private final String title = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "!!!";
    private final String sub = ChatColor.RED + "Your king is under attack!";

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Game game = KingdomDefense.getInstance().getGame();
        Entity entity = event.getEntity();
        Player player;
        if(event.getDamager() instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
            if(!(shooter instanceof Player)) {
                event.setCancelled(true);
                return;
            }
            player = (Player) shooter;
        } else if(event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else {
            return;
        }
        if(entity.hasMetadata("king")) {
            TeamType teamType = (TeamType) entity.getMetadata("king").get(0).value();
            King king = game.getKing(teamType);
            king.setHealth(king.getHealth() - event.getFinalDamage());
            game.moveCheck(player, player.getLocation());
            king.getTeam().getBukkitPlayers().stream().forEach(player1 -> PacketUtil.sendTitleToPlayer(player1, title, sub));
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(event.getPlayer());
        event.setCancelled(true);
        if(entity.hasMetadata("king")) {
            new PurchaseStructureMenu(info).open(info.getBukkitPlayer());
        }
    }

    @EventHandler
    public void onTowerDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if(!(entity instanceof Player)) {
            return;
        }
        if(entity.hasMetadata("damage")) {
            event.setDamage(event.getFinalDamage() + (event.getFinalDamage() * entity.getMetadata("damage").get(0).asDouble()));
        }
    }

    @EventHandler
    public void onBurn(EntityCombustEvent event) {
        if(event.getEntity().hasMetadata("noBurn")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(!entity.hasMetadata("king")) {
            return;
        }
        Game game = KingdomDefense.getInstance().getGame();
        TeamType teamType = (TeamType) entity.getMetadata("king").get(0).value();
        CSTeam team = game.getTeam(teamType);
        team.getBukkitPlayers().stream().forEach(player -> {
            player.sendMessage(ChatColor.RED + "Your Kig has fallen!");
            player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
            player.sendMessage(ChatColor.RED + "You are now out of the game.");
            player.sendMessage(ChatColor.RED + "Feel free to join another, or stay and spectate.");
            player.setGameMode(GameMode.SPECTATOR);
        });
    }

}
