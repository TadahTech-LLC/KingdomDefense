package com.tadahtech.fadecloud.kd.scoreboard;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class Gameboard {

    private Game game;
    private Map<UUID, SimpleScoreboard> objectives = Maps.newHashMap();

    public Gameboard(Game game) {
        this.game = game;
    }

    public void flip() {
        game.getPlayers().stream().forEach(info -> {
            Player player = info.getBukkitPlayer();
            SimpleScoreboard objective = objectives.get(player.getUniqueId());
            if (objective == null) {
                objective = new SimpleScoreboard(ChatColor.translateAlternateColorCodes('&', "&8&l(&bK&fD&8&l)"));
                player.setScoreboard(objective.getScoreboard());
                this.objectives.put(player.getUniqueId(), objective);
            }
            objective.add(color("&8&m-------"), 15);
            objective.add(ChatColor.GRAY + "Online: ", 13);
            objective.add(ChatColor.GREEN.toString() + game.getPlayers().size() + "/" + game.getMap().getMax(), 12);
            objective.add(color("&8&m&l-"), 11);
            objective.add(ChatColor.GRAY + "Coins: ", 10);
            objective.add(ChatColor.YELLOW.toString() + info.getCoins(), 9);
            objective.add(color("&8&m&l-&r"), 8);
            objective.add(color("&7Map"), 7);
            objective.add(ChatColor.RED + game.getMap().getName(), 6);
            objective.add(color("&8&m&o-&r"), 5);
            objective.add(color("&7Stage"), 4);
            String form = game.getState().format() + ChatColor.GRAY + " (" + ChatColor.GREEN + Utils.formatTime(game.getTimeLeft()) + ChatColor.GRAY + ")";
            objective.add(form, 3);
            objective.add(color("&8&m-------&r"), 2);
            objective.update();
        });
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void add(PlayerInfo info) {
        SimpleScoreboard objective = new SimpleScoreboard(ChatColor.translateAlternateColorCodes('&', "&8&l(&bK&fD&8&l)"));
        info.getBukkitPlayer().setScoreboard(objective.getScoreboard());
        this.objectives.putIfAbsent(info.getUuid(), objective);
    }
}
