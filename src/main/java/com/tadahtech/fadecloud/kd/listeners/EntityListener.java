package com.tadahtech.fadecloud.kd.listeners;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.econ.EconomyReward;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.king.King;
import com.tadahtech.fadecloud.kd.menu.menus.KingMenu;
import com.tadahtech.fadecloud.kd.shop.menu.ShopMainMenu;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Created by Timothy Andis (TadahTech) on 8/1/2015.
 */
public class EntityListener implements Listener {

    private final String title = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "!!! ";
    private final String sub = ChatColor.RED + "Your king is under attack!";

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Game game = KingdomDefense.getInstance().getGame();
        if(game.getState() != GameState.BATTLE) {
            event.setCancelled(true);
            return;
        }
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
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if(entity.hasMetadata("king")) {
            TeamType teamType = (TeamType) entity.getMetadata("king").get(0).value();
            if(info.getCurrentTeam().getType() == teamType) {
                event.setCancelled(true);
                return;
            }
            King king = game.getKing(teamType);
            king.hit(event.getFinalDamage());
            king.setLastDamager(info);
            return;
        }
        if(entity.getType() == EntityType.ZOMBIE) {
            if(!entity.hasMetadata("noBurn")) {
                return;
            }
            if(info.getCurrentTeam().getType() == TeamType.ZOMBIE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVoid(EntityDamageEvent event) {
        if(event.getCause() != DamageCause.VOID) {
            return;
        }
        Entity entity = event.getEntity();
        if(!entity.hasMetadata("king")) {
            return;
        }
        Game game = KingdomDefense.getInstance().getGame();
        if(game.getState() != GameState.BATTLE) {
            event.setCancelled(true);
            return;
        }
        TeamType teamType = (TeamType) entity.getMetadata("king").get(0).value();
        King king = game.getKing(teamType);
        PlayerInfo info = king.getLastDamager();
        if(info == null) {
            //odd.
            king.respawn(false);
            return;
        }
        Bukkit.getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) entity, Lists.newArrayList()));
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(event.getPlayer());
        event.setCancelled(true);
        if(entity.hasMetadata("king")) {
            if (info.getBukkitPlayer().hasMetadata("spectator")) {
                event.setCancelled(true);
                return;
            }
            TeamType teamType = (TeamType) entity.getMetadata("king").get(0).value();
            Game game = KingdomDefense.getInstance().getGame();
            King king = game.getKing(teamType);
            if(info.getCurrentTeam().getType() != teamType) {
                event.setCancelled(true);
                info.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Health: " + king.getHealth());
                return;
            }
            new KingMenu(info).open(event.getPlayer());
            info.getBukkitPlayer().playSound(info.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
        }
        if(entity instanceof Villager) {
            Villager villager = (Villager) entity;
            if(villager.isCustomNameVisible()) {
                new ShopMainMenu().open(info.getBukkitPlayer());
            }
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
        if(event.getEntity().hasMetadata("noBurn") || event.getEntity().hasMetadata("king")) {
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
        game.getKing(teamType).remove();
        CSTeam team = game.getTeam(teamType);
        game.getPlayers().stream().forEach(info1 -> {
            if (info1.getCurrentTeam().getType() != teamType) {
                return;
            }
            Player player = info1.getBukkitPlayer();
            player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "---------------------------");
            player.sendMessage(ChatColor.RED + "Your Kig has fallen!");
            player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
            player.sendMessage(ChatColor.RED + "You are now out of the game.");
            player.sendMessage(ChatColor.RED + "Feel free to join another, or stay and spectate.");
            player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "---------------------------");
            game.spectate(player);
        });

        game.setTeamsLeft(game.getTeamsLeft() - 1);
        team.setLost(true);
        Player killer = ((LivingEntity) entity).getKiller();
        if(killer == null) {
            if(game.getKing(teamType).getLastDamager() == null) {
                return;
            }
            killer = game.getKing(teamType).getLastDamager().getBukkitPlayer();
            if(killer == null) {
                return;
            }
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(killer);
        EconomyReward reward = new EconomyReward("Killing " + teamType.getName() + "'s King", 25);
        game.getEconomyManager().add(reward, info);
        final Player finalKiller = killer;
        game.getBukkitPlayers().stream().forEach(player -> {
            String title = finalKiller.getName();
            String sub = ChatColor.YELLOW + "killed " + teamType.fancy() + "'s" + ChatColor.YELLOW + " King!";
            PacketUtil.sendTitleToPlayer(player, title, sub);
            player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
        });
    }

    @EventHandler
    public void onTP(EntityTeleportEvent entityTeleportEvent) {
        if(entityTeleportEvent.getEntityType() == EntityType.ENDERMAN) {
            entityTeleportEvent.setCancelled(true);
        }
    }


}
