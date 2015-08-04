package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.HubItem;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.Packet;
import net.minecraft.server.v1_8_R2.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R2.PacketPlayInClientCommand.EnumClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class GameListener implements Listener {

    public static HubItem hubItem = new HubItem();

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        Game game = KingdomDefense.getInstance().getGame();
        if(event.getName().equalsIgnoreCase("TadahTech") || event.getName().equalsIgnoreCase("DaddyMew")) {
            return;
        }
        if(KingdomDefense.EDIT_MODE) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "This server is currently being edited.");
            return;
        }
        if(game == null) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "This server is currently being edited.");
            return;
        }
        if(game.getState() != GameState.WAITING) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "This game is currently in progress!");
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
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
        game.removePlayer(info);
        new GameInfoResponsePacket().write();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        String format = ChatColor.GRAY + "[" + info.getCurrentTeam().getType().fancy() + ChatColor.GRAY + "] ["+ ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + "] » " + ChatColor.WHITE + event.getMessage();
        if(info.isBeta()) {
            format = ChatColor.RED + "[" + ChatColor.GOLD + "Beta" + ChatColor.RED + "] " + format;
        }
        if (player.getGameMode() == GameMode.SPECTATOR) {
            Set<Player> players = event.getRecipients();
            players.stream().filter(player1 -> player1.getGameMode() == GameMode.SPECTATOR).collect(Collectors.toList());
            event.setCancelled(true);
            final String finalFormat = format;
            players.forEach(player1 -> player1.sendMessage("SPECTATOR " + finalFormat));
            return;
        }

        if(info.isTeamChat()) {
            Set<Player> players = event.getRecipients();
            players.stream().filter(player1 ->
              KingdomDefense.getInstance().getInfoManager().get(player1).getCurrentTeam().equals(info.getCurrentTeam()))
              .collect(Collectors.toList());
            event.setCancelled(true);
            event.setFormat(ChatColor.BLUE + "TEAM " + ChatColor.RESET + format);
            players.stream().forEach(player1 -> player.sendMessage(event.getFormat()));
            return;
        }
        event.setFormat(format);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        info.getCurrentTeam().onRespawn(player);
        hubItem.give(player, 8);
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if(event.getCause() != DamageCause.VOID) {
            return;
        }
        EntityDamageByEntityEvent last;
        if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            last = (EntityDamageByEntityEvent) player.getLastDamageCause();
        } else {
            player.setHealth(0);
            player.setMetadata("void", new FixedMetadataValue(KingdomDefense.getInstance(), true));
            return;
        }
        Entity lastDamager = last.getDamager();
        String name;
        if(lastDamager instanceof Player) {
            name = lastDamager.getName();
        } else {
            name = Utils.pretty(lastDamager.getType().name());
        }
        String message = ChatColor.BLUE + player.getName() + ChatColor.GRAY + "plummeted to his death at the hands of " + ChatColor.BLUE + name;
        Bukkit.broadcastMessage(message);
        player.setMetadata("noMessage", new FixedMetadataValue(KingdomDefense.getInstance(), 0));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Packet packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        new BukkitRunnable() {
            @Override
            public void run() {
                nmsPlayer.playerConnection.sendPacket(packet);
            }
        }.runTaskLater(KingdomDefense.getInstance(), 3L);

        Player killer = player.getKiller();

        if(killer == null) {
            return;
        }

        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        info.setDeaths(info.getDeaths() + 1);

        if(player.hasMetadata("noMessage")) {
            player.removeMetadata("noMessage", KingdomDefense.getInstance());
            return;
        }

        if(player.hasMetadata("void")) {
            player.removeMetadata("void", KingdomDefense.getInstance());
            Bukkit.broadcastMessage(ChatColor.BLUE + player.getName() + ChatColor.GRAY + " tried to fly, but failed.");
            return;
        }

        PlayerInfo killerInfo = KingdomDefense.getInstance().getInfoManager().get(killer);
        killerInfo.setKills(killerInfo.getKills() + 1);

        String inhand = "AIR";

        if(killer.getItemInHand() != null && killer.getItemInHand().getType() != Material.AIR) {
            inhand = Utils.pretty(killer.getItemInHand().getType().name());
        }

        PacketUtil.sendActionBarMessage(killer, ChatColor.GOLD + "+10 coins for killing " + ChatColor.BOLD + player.getName());

        killerInfo.setCoins(killerInfo.getCoins() + 10);
        killerInfo.getBukkitPlayer().playSound(killer.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

        String message = ChatColor.BLUE + killer.getName() + ChatColor.GRAY +  " killed " + ChatColor.BLUE + player.getName() + ChatColor.GRAY + (inhand.equalsIgnoreCase("AIR") ? "!" : " with " + inhand);

        Bukkit.broadcastMessage(message);
    }

}
