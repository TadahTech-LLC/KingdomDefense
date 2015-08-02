package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.HubItem;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class GameListener implements Listener {

    private HubItem hubItem = new HubItem();

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        Game game = KingdomDefense.getInstance().getGame();
        if(game.getState() != GameState.WAITING) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "This game is currently in progress!");
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Game game = KingdomDefense.getInstance().getGame();
        game.addPlayer(info);
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
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        String format = info.getCurrentTeam().getType().fancy() + ChatColor.GRAY + " ["+ player.getDisplayName() + ChatColor.GRAY + "] » " + ChatColor.GRAY + event.getMessage();
        if(info.isTeamChat()) {
            Set<Player> players = event.getRecipients();
            players.stream().filter(player1 ->
              KingdomDefense.getInstance().getInfoManager().get(player1).getCurrentTeam().equals(info.getCurrentTeam()))
              .collect(Collectors.toList());
            event.setFormat(ChatColor.BLUE + "TEAM " + ChatColor.RESET + format);
            return;
        }
        event.setFormat(format);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        hubItem.give(player, 8);
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
        PlayerInfo killerInfo = KingdomDefense.getInstance().getInfoManager().get(killer);

        killerInfo.setKills(killerInfo.getKills() + 1);
        info.setDeaths(info.getDeaths() + 1);

        String inhand = "";

        if(killer.getItemInHand() != null) {
            inhand = Utils.pretty(killer.getItemInHand().getType().name());
        }

        String message = ChatColor.BLUE + killer.getName() + " killed " + player.getName() + (inhand.equalsIgnoreCase("AIR") ? "!" : " with " + inhand);

        Bukkit.broadcastMessage(message);
    }
}
