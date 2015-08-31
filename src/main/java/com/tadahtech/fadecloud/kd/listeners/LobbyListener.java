package com.tadahtech.fadecloud.kd.listeners;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.Packet;
import com.tadahtech.fadecloud.kd.csc.packets.request.JoinGameRequestPacket;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.shop.shops.KitShop;
import com.tadahtech.fadecloud.kd.sign.LobbySign;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class LobbyListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        if(!event.getLine(0).equalsIgnoreCase("[KD]")) {
            return;
        }
        String arena = event.getLine(1);
        Optional<LobbySign> maybeSign = LobbySign.get(arena);
        if(!maybeSign.isPresent()) {
            LobbySign lobbySign = new LobbySign(arena);
            lobbySign.create((Sign) block.getState(), event);
        } else {
            maybeSign.get().create((Sign) block.getState(), event);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() == Material.AIR) {
            return;
        }
        if (!(block.getState() instanceof Sign)) {
            return;
        }
        event.setCancelled(true);
        Sign sign = (Sign) block.getState();
        String[] lines_ = sign.getLines();
        List<String> lines = Lists.newArrayList(lines_).stream().map(ChatColor::stripColor).collect(Collectors.toList());
        String first = lines.get(0);
        if (!first.equalsIgnoreCase("[Join]")) {
            return;
        }
        String uiname = lines.get(1);
        String server = KingdomDefense.getInstance().getServerNames().get(uiname);
        Packet packet = new JoinGameRequestPacket(server, player);
        if (!LobbySign.get(uiname).isPresent()) {
            new LobbySign(uiname).getSigns().add(sign.getLocation());
            //Fuck bukkit man.
        }
        packet.write();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(event.getPlayer());
        KingdomDefense.getInstance().getLobbyboard().add(info);
        KingdomDefense.getInstance().getLobbyboard().flip();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        KingdomDefense.getInstance().getLobbyboard().remove(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(event.getPlayer());
        event.setCancelled(true);
        if(entity instanceof Villager) {
            Villager villager = (Villager) entity;
            if(villager.isCustomNameVisible()) {
                KitShop.INSTANCE.getMenu(info.getBukkitPlayer()).open(info.getBukkitPlayer());
            }
        }
    }
}
