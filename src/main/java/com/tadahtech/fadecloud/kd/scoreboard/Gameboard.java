package com.tadahtech.fadecloud.kd.scoreboard;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import pw.teg.fadecrystals.FadeCrystals;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class Gameboard {

    private Game game;
    private Map<UUID, BufferedObjective> objectives = Maps.newHashMap();

    public Gameboard(Game game) {
        this.game = game;
    }

    public void flip() {
        game.getPlayers().stream().forEach(info -> {
            Player player = info.getBukkitPlayer();
            BufferedObjective objective = objectives.get(player.getUniqueId());
            if (objective == null) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                objective = new BufferedObjective(scoreboard);
                player.setScoreboard(scoreboard);
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setTitle(ChatColor.AQUA.toString() + ChatColor.BOLD + "fadecloudmc.com");
                this.objectives.putIfAbsent(player.getUniqueId(), objective);
            }
            objective.setNextLine(ChatColor.BLUE + "----- Kingdom Defense -----");
            objective.blank();
            objective.setNextLine(ChatColor.GREEN + "State: ");
            objective.setNextLine(game.getState().format());
            objective.blank();
            objective.setNextLine(ChatColor.GREEN + "Time Remaining: ");
            objective.setNextLine(ChatColor.WHITE.toString() + Utils.formatTime(game.getTimeLeft()));
            objective.blank();
            objective.setNextLine(ChatColor.GREEN + "Coins: ");
            objective.setNextLine(ChatColor.WHITE.toString() + info.getCoins());
            objective.blank();
            objective.setNextLine(ChatColor.GREEN + "Crystals: ");
            objective.setNextLine(ChatColor.WHITE.toString() + ChatColor.RESET + FadeCrystals.get().getPlayer(info.getBukkitPlayer()).getCrystals());
            objective.flip();
        });
    }

    public void add(PlayerInfo info) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        info.getBukkitPlayer().setScoreboard(scoreboard);
        BufferedObjective objective = new BufferedObjective(scoreboard);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setTitle(ChatColor.GREEN.toString() + ChatColor.BOLD + "Kingdom Defense");
        this.objectives.putIfAbsent(info.getUuid(), objective);
    }
}
