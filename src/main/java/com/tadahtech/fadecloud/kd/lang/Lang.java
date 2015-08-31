package com.tadahtech.fadecloud.kd.lang;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Timothy Andis
 */
public enum Lang {

    NO_GAME_FOUND("Could not find a game on %server%."),
    GAME_FOUND("Connecting you to %server%"),
    GAME_FULL("%server% is currently full, try another!"),
    GAME_IN_EDIT("%game% is currently behind repaired."),
    HEALTH_BAR("%hearts% &7(%health%)"),
    GAME_JOIN("You have joined the %team% team!"),
    GAME_START("Put game information here Alex"),

    CHEST_FOUND("You found a chest mining!"),

    KIT_EQUIPPED("You have equipped the %kit% kit"),
    KIT_NOT_UNLOCKED("You have not unlocked the %kit% kit"),
    KIT_UNLOCKED("Successfully purchased %kit%!"),
    KIT_OWNED("You already own %kit%"),
    KIT_ALREADY_CHOSEN("You have already chose %kit%"),

    ITEM_BOUGHT("Purchased %item% for %price%"),

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
    TIME_LEFT("%minutes% left!"),
    KING_BEING_ATTACKED("Your king is being attacked!"),
    ;


    private static Map<Lang, List<String>> messages = Maps.newHashMap();
    private String defaultMessage;
    public static String PREFIX = (ChatColor.DARK_GRAY.toString() + "[" + ChatColor.AQUA + "KD" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY);

    Lang(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    static {
        writeValues();
    }

    public void send(PlayerInfo info, ImmutableMap<String, String> objects) {
        if(messages.get(this) == null) {
            writeValues();
            return;
        }
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
        if(messages.get(this) == null) {
            writeValues();
            return;
        }
        for (String s : messages.get(this)) {
            String base = s;
            base = base.replace("%player%", player.getName());
            for (Entry<String, String> entry : objects.entrySet()) {
                base = base.replace("%" + entry.getKey() + "%", entry.getValue());
            }
            player.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', base));
        }
    }

    public void send(Player player) {
        send(player, ImmutableMap.of());
    }

    public void send(PlayerInfo info) {
        if(messages.get(this) == null) {
            writeValues();
            return;
        }
        for (String s : messages.get(this)) {
            String base = s;
            base = base.replace("%player%", info.getBukkitPlayer().getName());
            info.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', base));
        }
    }

    public static void writeValues() {
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
                config.set(lang.name().toLowerCase().replace("_", "-"), Lists.newArrayList(lang.defaultMessage));
                try {
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String s : config.getKeys(false)) {
            Lang lang = Lang.valueOf(s.toUpperCase().replace("-", "_"));
            List<String> list = config.getStringList(s);
            messages.put(lang, list);
        }
    }

}
