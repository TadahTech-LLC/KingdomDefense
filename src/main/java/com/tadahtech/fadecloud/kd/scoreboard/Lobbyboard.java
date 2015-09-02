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

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class Lobbyboard {

    private Map<UUID, SimpleScoreboard> objectives;
    private InfoManager infoManager;

    public Lobbyboard() {
        this.objectives = Maps.newHashMap();
        this.infoManager = KingdomDefense.getInstance().getInfoManager();
    }

    public void flip() {
        List<Player> players = Lists.newArrayList(Bukkit.getOnlinePlayers());
        players.forEach(player -> {
            PlayerInfo info = infoManager.get(player);
            if (info == null) {
                KingdomDefense.getInstance().getInfoStore().load(player.getUniqueId());
                return;
            }
            SimpleScoreboard objective = objectives.get(player.getUniqueId());
            String wins = ChatColor.AQUA.toString() + (info.getWins(TeamType.CREEPER)  + info.getWins(TeamType.ZOMBIE)
              + info.getWins(TeamType.SKELETON) + info.getWins(TeamType.ENDERMAN));
            String kills = ChatColor.getByChar('a').toString() + info.getKills();
            String deaths = ChatColor.getByChar('4').toString() + info.getDeaths();
            String coins = ChatColor.getByChar('e').toString() + info.getCoins();
            if (objective == null) {
                objective = new SimpleScoreboard(ChatColor.translateAlternateColorCodes('&', "&8&l(&bK&fD&8&l)"));
                objective.send(player);
                this.objectives.putIfAbsent(player.getUniqueId(), objective);
            }
            objective.setLine(15, color("&8&m-------"));
            objective.setLine(13, ChatColor.GRAY + "Wins: ");
            objective.setLine(12, wins);
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
            objective.update();
        });
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void add(PlayerInfo info) {
        this.objectives.remove(info.getUuid());
        SimpleScoreboard objective = new SimpleScoreboard(ChatColor.translateAlternateColorCodes('&', "&8&l(&bK&fD&8&l)"));
        objective.send(info.getBukkitPlayer());
        this.objectives.putIfAbsent(info.getUuid(), objective);
    }

    public void remove(Player player) {
        this.objectives.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
