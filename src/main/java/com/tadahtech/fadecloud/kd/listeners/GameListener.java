package com.tadahtech.fadecloud.kd.listeners;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.misc.ChooseTeamItem;
import com.tadahtech.fadecloud.kd.items.misc.HubItem;
import com.tadahtech.fadecloud.kd.items.misc.ShopReItem;
import com.tadahtech.fadecloud.kd.items.misc.SpectatorItem;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class GameListener implements Listener {

    public static HubItem HUB = new HubItem();
    public static SpectatorItem SPECTATOR = new SpectatorItem();
    public static ChooseTeamItem TEAM = new ChooseTeamItem();

    public static List<String> NAMES = Lists.newArrayList("iBagel");

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        Game game = KingdomDefense.getInstance().getGame();
        if (event.getName().equalsIgnoreCase("BilboBaggins") || event.getName().equalsIgnoreCase("DaddyMew") ||
          event.getName().equalsIgnoreCase("Eriic") || event.getName().equalsIgnoreCase("RevengeBlade") || NAMES.contains(event.getName())) {
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
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        if(KingdomDefense.EDIT_MODE) {
            return;
        }
        event.setJoinMessage(null);
        Player player = event.getPlayer();

        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                Game game = KingdomDefense.getInstance().getGame();
                if(game.getState() != GameState.WAITING && game.getState() != GameState.COUNTDOWN) {
                    game.spectate(player);
                    return;
                }
                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().setArmorContents(null);
                player.setFoodLevel(20);
                player.setMaxHealth(20);
                player.setWalkSpeed(0.2F);
                player.setHealth(20.0D);

                game.addPlayer(info);
                HUB.give(player, 8);
                TEAM.give(player, 0);
                new ShopReItem().give(player, 2);
            }
        }.runTaskLater(KingdomDefense.getInstance(), 2l);
        new GameInfoResponsePacket().write();

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Game game = KingdomDefense.getInstance().getGame();
        game.removePlayer(info);
        game.flip();
        new GameInfoResponsePacket().write();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if(KingdomDefense.getInstance().getGame() == null) {
            return;
        }
        Game game = KingdomDefense.getInstance().getGame();
        if(game.getState() != GameState.BATTLE && game.getState() != GameState.PEACE) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasMetadata("spectator")) {
            HUB.give(player, 8);
            event.setRespawnLocation(game.getLobby());
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        event.setRespawnLocation(info.getCurrentTeam().getRespawn());
        info.getCurrentTeam().onRespawn(player);

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
            name = ((Player)lastDamager).getName();
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
        String message = ChatColor.AQUA + player.getName() + ChatColor.GRAY + " plummeted to his death at the hands of " + ChatColor.AQUA + name;
        Bukkit.broadcastMessage(message);
        player.setMetadata("noMessage", new FixedMetadataValue(KingdomDefense.getInstance(), 0));
    }

    @EventHandler
    public void onArmorClick(InventoryClickEvent event) {

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);

        Player player = event.getEntity();

        player.spigot().respawn();

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
            Bukkit.broadcastMessage(ChatColor.AQUA + player.getName() + ChatColor.GRAY + " tried to fly, but failed.");
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

        String message = ChatColor.AQUA + killer.getName() + ChatColor.GRAY + " killed " + ChatColor.AQUA + player.getName() + ChatColor.GRAY + (inhand.equalsIgnoreCase("AIR") ? " with his bare hands. Savage!" : " with " + inhand);

        Bukkit.broadcastMessage(message);
    }

}
