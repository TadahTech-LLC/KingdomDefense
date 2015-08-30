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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class LobbySign {

    public static Map<String, LobbySign> signMap = new HashMap<>();

    private String arena, ui;
    private List<Location> signs;
    private static final String FIRST = ChatColor.translateAlternateColorCodes('&', "&a[Join]");

    public LobbySign(String arena) {
        this.arena = arena;
        this.signs = new ArrayList<>();
        signMap.putIfAbsent(arena, this);
    }

    public LobbySign(String arena, List<Location> signs) {
        this.arena = arena;
        this.signs = signs;
        signMap.putIfAbsent(arena, this);
        String to = KingdomDefense.getInstance().getServerNames().get(arena);
        Packet packet = new GameInfoRequestPacket(to, arena);
        packet.write();
    }

    public void create(Sign sign, SignChangeEvent event) {
        signs.add(sign.getBlock().getLocation());
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
        String to = KingdomDefense.getInstance().getServerNames().get(arena);
        Packet packet = new GameInfoRequestPacket(to, arena);
        packet.write();
    }

    public void update(GameInfoResponsePacket responsePacket) {
        List<Location> signs = Lists.newArrayList(this.signs);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location location : signs) {
                    Sign sign = (Sign) location.getBlock().getState();
                    if(sign.getLocation().getBlock().getRelative(BlockFace.NORTH).getType() == Material.AIR) {
                        continue;
                    }
                    sign.setLine(0, FIRST);
                    sign.setLine(1, ChatColor.BLUE + responsePacket.arena);
                    String players = ChatColor.DARK_BLUE + "(" + responsePacket.players + " / " + responsePacket.max + ")";
                    sign.setLine(2, players);
                    sign.setLine(3, responsePacket.state.format());
                    sign.update(true);
                    Bukkit.getPluginManager().callEvent(new SignChangeEvent(sign.getBlock(), null, sign.getLines() ));
                }
            }
        }.runTask(KingdomDefense.getInstance());
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

    public List<Location> getSigns() {
        return signs;
    }

    public static void load(FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("signs");
        if (section == null) {
            return;
        }
        for (String s : section.getKeys(false)) {
            List<String> signsRaw = section.getStringList(s + ".locations");
            List<Location> signs = Lists.newArrayList();
            for (String raw : signsRaw) {
                Location location = Utils.locFromString(raw);
                signs.add(location);
            }
            new LobbySign(s, signs);
        }
        KingdomDefense.getInstance().getLogger().info("Loaded signs...");
    }

    public Map<String, Object> save() {
        Map<String, Object> map = Maps.newHashMap();
        List<String> signs = Lists.newArrayList();
        signs.addAll(this.signs.stream().map(Utils::locToString).collect(Collectors.toList()));
        map.put("locations", signs);
        return map;
    }

    public void updateNoResponse() {
        String red = ChatColor.DARK_RED + "██████████████";
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location location : signs) {
                    Sign sign = (Sign) location.getBlock().getState();
                    sign.setLine(0, red);
                    sign.setLine(1, ChatColor.BLUE + arena);
                    sign.setLine(2, red);
                    sign.setLine(3, GameState.DOWN.format());
                    sign.update(true);
                }
            }
        }.runTask(KingdomDefense.getInstance());
    }
}
