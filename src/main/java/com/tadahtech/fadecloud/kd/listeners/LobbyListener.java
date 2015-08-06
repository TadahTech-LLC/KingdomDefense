package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.Packet;
import com.tadahtech.fadecloud.kd.csc.packets.request.JoinGameRequestPacket;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.sign.LobbySign;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
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
        Sign sign = (Sign) block.getState();
        String[] lines_ = sign.getLines();
        List<String> lines = new ArrayList<>(Arrays.asList(lines_).stream().map(ChatColor::stripColor).collect(Collectors.toList()));
        String first = lines.get(0);
        if (!first.equalsIgnoreCase("[Join]")) {
            return;
        }
        String uiname = lines.get(1);
        String server = KingdomDefense.getInstance().getServerNames().get(uiname);
        Packet packet = new JoinGameRequestPacket(server, player);
        if (!LobbySign.get(uiname).isPresent()) {
            new LobbySign(uiname);
            //Fuck bukkit man.
            SignChangeEvent signChangeEvent = new SignChangeEvent(block, player, new String[]{"[KD]", uiname, " ", " "});
            Bukkit.getPluginManager().callEvent(signChangeEvent);
        }
        packet.write();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(event.getPlayer());
        ItemBuilder builder = new ItemBuilder(new ItemStack(Material.SKULL_ITEM));
        builder.data((byte) 3);
        builder.name(ChatColor.AQUA.toString() + "Profile");
        builder.lore(ChatColor.GRAY + "Right click to view your profile");
        builder.setOwner(info.getBukkitPlayer().getName());
        if(info.getBukkitPlayer().getItemInHand().getType() == Material.SKULL_ITEM) {
            return;
        }
        info.getBukkitPlayer().getInventory().setItem(1, builder.build());
        KingdomDefense.getInstance().getLobbyboard().add(info);
        KingdomDefense.getInstance().getLobbyboard().flip();
    }
}
