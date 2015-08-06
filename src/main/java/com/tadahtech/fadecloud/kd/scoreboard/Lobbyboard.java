package com.tadahtech.fadecloud.kd.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.InfoManager;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class Lobbyboard {

    private Map<UUID, BufferedObjective> objectives;
    private InfoManager infoManager;

    public Lobbyboard() {
        this.objectives = Maps.newHashMap();
        this.infoManager = KingdomDefense.getInstance().getInfoManager();
    }

    public void flip() {
        List<Player> players = Lists.newArrayList(Bukkit.getOnlinePlayers());
        players.stream().map(infoManager::get).collect(Collectors.toList()).forEach(info -> {
            Player player = info.getBukkitPlayer();
            BufferedObjective objective = objectives.get(player.getUniqueId());
            String creeper_wins = ChatColor.GREEN.toString() + info.getWins(TeamType.CREEPER);
            String zombie_wins = ChatColor.DARK_GREEN.toString() + info.getWins(TeamType.ZOMBIE);
            String skeleton_wins = ChatColor.GRAY.toString() + info.getWins(TeamType.SKELETON);
            String enderman_wins = ChatColor.LIGHT_PURPLE.toString() + info.getWins(TeamType.ENDERMAN);
            String kills = ChatColor.getByChar('b').toString() + info.getKills();
            String deaths = ChatColor.getByChar('4').toString() + info.getDeaths();
            String coins = ChatColor.getByChar('e').toString() + info.getCoins();
            if (objective == null) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                objective = new BufferedObjective(scoreboard);
                player.setScoreboard(scoreboard);
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setTitle(ChatColor.translateAlternateColorCodes('&', "&8&l(&bK&fD&8&l)"));
                this.objectives.putIfAbsent(player.getUniqueId(), objective);
            }
            objective.setLine(15, color("&8&m-------"));
            objective.setLine(13, ChatColor.GRAY + "Wins: ");
            objective.setLine(12, creeper_wins + "-" + zombie_wins + "-" + skeleton_wins + "-" + enderman_wins);
            objective.setLine(11, color("&8&m&l-"));
            objective.setLine(10, ChatColor.GRAY + "Kills: ");
            objective.setLine(9, kills);
            objective.setLine(8, color("&8&m&l-&r"));
            objective.setLine(7, color("&7Deaths"));
            objective.setLine(6, deaths);
            objective.setLine(5, color("&8&m&o-&r"));
            objective.setLine(4, color("&7Coins"));
            objective.setLine(3, coins);
            objective.setLine(2, color("&8&m-------&r"));
            objective.flip();
        });
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void add(PlayerInfo info) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        info.getBukkitPlayer().setScoreboard(scoreboard);
        BufferedObjective objective = new BufferedObjective(scoreboard);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setTitle(ChatColor.translateAlternateColorCodes('&', "&8&l(&bK&fD&8&l)"));
        this.objectives.putIfAbsent(info.getUuid(), objective);
    }

}
