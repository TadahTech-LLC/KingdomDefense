package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.HubItem;
import com.tadahtech.fadecloud.kd.map.LocationType;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class GameListener implements Listener {

    public static HubItem hubItem = new HubItem();

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        Game game = KingdomDefense.getInstance().getGame();
        if (event.getName().equalsIgnoreCase("TadahTech") || event.getName().equalsIgnoreCase("DaddyMew")) {
            return;
        }
        if (KingdomDefense.EDIT_MODE) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "This server is currently being edited.");
            return;
        }
        if (game == null) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "This server is currently being edited.");
            return;
        }
        if (game.getState() != GameState.WAITING && game.getState() != GameState.COUNTDOWN) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "This game is currently in progress!");
            return;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(KingdomDefense.EDIT_MODE) {
            return;
        }
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFoodLevel(20);
        player.setMaxHealth(20);
        player.setWalkSpeed(0.2F);
        player.setHealth(20.0D);
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Game game = KingdomDefense.getInstance().getGame();
        game.addPlayer(info);
        new GameInfoResponsePacket().write();
        String top = ChatColor.AQUA.toString() + ChatColor.BOLD + "Kingdom Defense";
        String bottom = ChatColor.GOLD.toString() + ChatColor.BOLD + "fadecloudmc.com";
        PacketUtil.sendTabToPlayer(player, top, bottom);
        hubItem.give(player, 8);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Game game = KingdomDefense.getInstance().getGame();
        game.flip();
        game.removePlayer(info);
        new GameInfoResponsePacket().write();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Game game = KingdomDefense.getInstance().getGame();
        if (game == null) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        StringBuilder builder = new StringBuilder();
        List<Player> players = game.getBukkitPlayers();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            players = game.getBukkitPlayers().stream().filter(player1 -> player1.getGameMode() == GameMode.SPECTATOR)
              .collect(Collectors.toList());
            builder.append(ChatColor.DARK_GRAY)
              .append("[").append(ChatColor.DARK_AQUA).append("SPECTATOR").append(ChatColor.DARK_GRAY).append("] ");
            builder.append(ChatColor.AQUA).append(player.getName());
            builder.append(" ").append(ChatColor.GRAY).append("»").append(ChatColor.WHITE).append(" ").append(event.getMessage());
            players.stream().forEach(player1 -> player1.sendMessage(builder.toString()));
            return;
        }
        if (info.isTeamChat()) {
            builder.append(ChatColor.BLUE).append("[TEAM] ");
            players = game.getPlayers().stream()
              .filter(info1 -> info.getCurrentTeam().equals(info1.getCurrentTeam()))
              .map(PlayerInfo::getBukkitPlayer)
              .collect(Collectors.toList());
        }
        if (info.isBeta()) {
            builder.append(ChatColor.RED).append("[").append(ChatColor.GOLD).append("BETA").append(ChatColor.RED).append("] ").append(ChatColor.RESET);
        }
        if (info.getCurrentTeam() != null) {
            builder.append(ChatColor.DARK_GRAY).append("[");
            builder.append(info.getCurrentTeam().getType().fancy());
            builder.append(ChatColor.DARK_GRAY).append("] ");
        }
        builder.append(ChatColor.AQUA).append(player.getName());
        builder.append(" ").append(ChatColor.GRAY).append("»").append(ChatColor.WHITE).append(" ").append(event.getMessage());
        players.stream().forEach(player1 -> player1.sendMessage(builder.toString()));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        info.getCurrentTeam().onRespawn(player);
        event.setRespawnLocation(info.getCurrentTeam().getRespawn());
        if (player.getGameMode() == GameMode.SPECTATOR) {
            hubItem.give(player, 8);
        }
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Game game = KingdomDefense.getInstance().getGame();
        if (game.getState() == GameState.COUNTDOWN || game.getState() == GameState.WAITING) {
            event.setCancelled(true);
            if(event.getCause() == DamageCause.VOID && game.getState() != GameState.PEACE) {
                player.teleport(KingdomDefense.getInstance().getMap().getLocation(LocationType.LOBBY).get());
            } else if(game.getState() == GameState.PEACE) {
                player.teleport(info.getCurrentTeam().getRespawn());
            }
            return;
        }
        if (event.getCause() != DamageCause.VOID) {
            return;
        }
        EntityDamageByEntityEvent last;
        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            last = (EntityDamageByEntityEvent) player.getLastDamageCause();
        } else {
            player.setHealth(0);
            player.setMetadata("void", new FixedMetadataValue(KingdomDefense.getInstance(), true));
            return;
        }
        Entity lastDamager = last.getDamager();
        String name;
        if (lastDamager instanceof Player) {
            name = lastDamager.getName();
            Player killer = (Player) lastDamager;
            PlayerInfo killerInfo = KingdomDefense.getInstance().getInfoManager().get((Player) lastDamager);
            killerInfo.setKills(killerInfo.getKills() + 1);
            killerInfo.setCoins(killerInfo.getCoins() + 10);
            killerInfo.getBukkitPlayer().playSound(killer.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            PacketUtil.sendActionBarMessage(killer, ChatColor.GOLD + "+10 coins for killing " + ChatColor.BOLD + player.getName());
        } else {
            name = Utils.pretty(lastDamager.getType().name());
        }

        info.setDeaths(info.getDeaths() + 1);
        String message = ChatColor.BLUE + player.getName() + ChatColor.GRAY + " plummeted to his death at the hands of " + ChatColor.BLUE + name;
        Bukkit.broadcastMessage(message);
        player.setMetadata("noMessage", new FixedMetadataValue(KingdomDefense.getInstance(), 0));
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);

        Player player = event.getEntity();

        Player killer = player.getKiller();

        if (killer == null) {
            return;
        }

        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        info.setDeaths(info.getDeaths() + 1);

        if (player.hasMetadata("noMessage")) {
            player.removeMetadata("noMessage", KingdomDefense.getInstance());
            return;
        }

        if (player.hasMetadata("void")) {
            player.removeMetadata("void", KingdomDefense.getInstance());
            Bukkit.broadcastMessage(ChatColor.BLUE + player.getName() + ChatColor.GRAY + " tried to fly, but failed.");
            return;
        }

        PlayerInfo killerInfo = KingdomDefense.getInstance().getInfoManager().get(killer);
        killerInfo.setKills(killerInfo.getKills() + 1);

        String inhand = "AIR";

        if (killer.getItemInHand() != null && killer.getItemInHand().getType() != Material.AIR) {
            inhand = Utils.pretty(killer.getItemInHand().getType().name());
        }

        PacketUtil.sendActionBarMessage(killer, ChatColor.GOLD + "+10 coins for killing " + ChatColor.BOLD + player.getName());

        killerInfo.setCoins(killerInfo.getCoins() + 10);
        killerInfo.getBukkitPlayer().playSound(killer.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

        String message = ChatColor.BLUE + killer.getName() + ChatColor.GRAY + " killed " + ChatColor.BLUE + player.getName() + ChatColor.GRAY + (inhand.equalsIgnoreCase("AIR") ? "!" : " with " + inhand);

        Bukkit.broadcastMessage(message);
    }

}
