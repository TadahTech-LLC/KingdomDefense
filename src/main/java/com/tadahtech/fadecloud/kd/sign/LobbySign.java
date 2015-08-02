package com.tadahtech.fadecloud.kd.sign;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.Packet;
import com.tadahtech.fadecloud.kd.csc.packets.request.GameInfoRequestPacket;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.SignChangeEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class LobbySign {

    public static Map<String, LobbySign> signMap = new HashMap<>();

    private String arena;
    private List<Sign> signs;
    private static final String FIRST = ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "=<" +
            ChatColor.GREEN + "KD" +
            ChatColor.DARK_RED + ChatColor.STRIKETHROUGH + ">=";

    public LobbySign(String arena) {
        this.arena = arena;
        this.signs = new ArrayList<>();
        signMap.putIfAbsent(arena, this);
    }

    public LobbySign(String arena, List<Sign> signs) {
        this.arena = arena;
        this.signs = signs;
        signMap.putIfAbsent(arena, this);
    }

    public void create(Sign sign, SignChangeEvent event) {
        signs.add(sign);
        event.setLine(0, FIRST);
        String name = arena;
        name = ChatColor.BLUE + name;
        event.setLine(1, name);
        String players = ChatColor.DARK_BLUE + "(" + 0 + " / " + "?" + ")";
        event.setLine(2, players);
        GameState state = GameState.PINGING;
        event.setLine(3, state.format());
        event.getBlock().getState().update();
        sign.update(true);
        Packet packet = new GameInfoRequestPacket(arena);
        packet.write();
    }

    public void update(GameInfoResponsePacket responsePacket) {
        for(Sign sign : signs) {
            sign.setLine(0, FIRST);
            sign.setLine(1, ChatColor.BLUE + responsePacket.arena);
            String players = ChatColor.DARK_BLUE + "(" + responsePacket.players + " / " + responsePacket.max + ")";
            sign.setLine(2, players);
            sign.setLine(3, responsePacket.state.format());
            sign.update(true);
        }
    }

    public static Collection<LobbySign> getAll() {
        return signMap.values();
    }

    public static Optional<LobbySign> get(String arena) {
        return Optional.ofNullable(signMap.get(arena));
    }

    public String getArena() {
        return arena;
    }

    public List<Sign> getSigns() {
        return signs;
    }

    public static void load(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("signs");
        if(section == null) {
            return;
        }
        for(String s : section.getKeys(false)) {
            List<String> signsRaw = section.getStringList(s + ".locations");
            List<Sign> signs = Lists.newArrayList();
            for(String raw : signsRaw) {
                Location location = Utils.locFromString(raw);
                Block block = location.getBlock();
                BlockState state = block.getState();
                if(!(state instanceof Sign)) {
                    return;
                }
                signs.add((Sign) state);
            }
            new LobbySign(s, signs);
        }
        KingdomDefense.getInstance().getLogger().info("Loaded signs...");
    }

    public Map<String, Object> save() {
        Map<String, Object> map = Maps.newHashMap();
        List<String> signs = Lists.newArrayList();
        signs.addAll(this.signs.stream().map(sign -> Utils.locToString(sign.getLocation())).collect(Collectors.toList()));
        map.put("locations", signs);
        return map;
    }

}
