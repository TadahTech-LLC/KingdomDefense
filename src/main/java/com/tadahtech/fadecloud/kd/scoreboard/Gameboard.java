package com.tadahtech.fadecloud.kd.scoreboard;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

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
        int zombie = game.getTeamCount(TeamType.ZOMBIE);
        int skeleton = game.getTeamCount(TeamType.SKELETON);
        int enderman = game.getTeamCount(TeamType.ENDERMAN);
        int creeper = game.getTeamCount(TeamType.CREEPER);
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
            objective.setNextLine(ChatColor.GREEN + "Creeper Team Alive: ");
            objective.setNextLine(ChatColor.GREEN.toString() + creeper);
            objective.blank();
            objective.setNextLine(ChatColor.GREEN + "Zombie Team Alive: ");
            objective.setNextLine(ChatColor.DARK_GREEN.toString() + zombie);
            objective.blank();
            objective.setNextLine(ChatColor.GREEN + "Enderman Team Alive: ");
            objective.setNextLine(ChatColor.LIGHT_PURPLE.toString() + enderman);
            objective.blank();
            objective.setNextLine(ChatColor.GREEN + "Skeleton Team Alive");
            objective.setNextLine(ChatColor.GRAY.toString() + skeleton);
            objective.blank();
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
