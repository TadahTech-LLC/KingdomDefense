package com.tadahtech.fadecloud.kd.lang;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Timothy Andis
 */
public enum Lang {

    NO_GAME_FOUND("Couldn't find a game on %server%."),
    GAME_FOUND("Connecting you to %server%"),
    GAME_FULL("%server% is currently full, try another!"),
    GAME_IN_EDIT("%game% is currently behind repaired."),
    NO_PERMISSION("I'm sorry, but you don't have permission to do this!"),
    NOT_ENOUGH_COINS("You don't have enough coins! You need %total% for it. (%remainder% needed)"),
    PLAYER_ALREADY_IN_PARTY("%other% is already in your party!"),
    PLAYER_IN_DIFFERENT_PARTY("%other% is already in a different party!"),
    PLAYER_ALREADY_INVITED("%other% already has a pending party invite!"),
    PARTY_INVITE_SUCCESS("Invited %other% to your party. They have 30 seconds to accept."),
    PARTY_INVITED_TO("You've been invited to %other%'s party. type /party %other% to accept"),
    GAME_FOUND_TO_SPECTATE("%server% is currently mid game, sending you to spectate!"),
    PARTY_NOT_INVITED("You are not invited to that party!"),
    PARTY_JOIN("%other% has joined the party"),
    SPECTATING_PLAYER("Spectating %other%"),
    PLAYER_NOT_FOUND("Could not find a player by that name online!"),
    ;

    private static Map<Lang, List<String>> messages = Maps.newHashMap();
    private String defaultMessage;
    private String PREFIX = (ChatColor.GRAY.toString() + "[" + ChatColor.AQUA + "KingdomDefense" + ChatColor.GRAY + "]");

    Lang(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    static {
        KingdomDefense defense = KingdomDefense.getInstance();
        File file = new File(defense.getDataFolder(), "lang.yml");
        boolean write = false;
        if (!file.exists()) {
            write = true;
            try {
                if (!file.createNewFile()) {
                    defense.getLogger().severe("Failed to create lang.yml!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (write) {
            for (Lang lang : Lang.values()) {
                config.set(lang.name().toLowerCase().replace("_", "-"), Collections.singletonList(lang.defaultMessage));
            }
        }
        for (String s : config.getKeys(false)) {
            Lang lang = Lang.valueOf(s.toUpperCase().replace("-", "_"));
            List<String> list = config.getStringList(s);
            messages.put(lang, list);
        }
    }

    public void send(PlayerInfo info, ImmutableMap<String, String> objects) {
        for (String s : messages.get(this)) {
            String base = s;
            base = base.replace("%player%", info.getBukkitPlayer().getName());
            base = base.replace("%coins%", String.valueOf(info.getCoins()));
            for (Entry<String, String> entry : objects.entrySet()) {
                base = base.replace("%" + entry.getKey() + "%", entry.getValue());
            }
            info.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', base));
        }
    }

    public void send(Player player, ImmutableMap<String, String> objects) {
        for (String s : messages.get(this)) {
            String base = s;
            base = base.replace("%player%", player.getName());
            for (Entry<String, String> entry : objects.entrySet()) {
                base = base.replace("%" + entry.getKey() + "%", entry.getValue());
            }
            player.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', base));
        }
    }

    public void send(PlayerInfo info) {
        for (String s : messages.get(this)) {
            String base = s;
            base = base.replace("%player%", info.getBukkitPlayer().getName());
            info.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', base));
        }
    }

}
