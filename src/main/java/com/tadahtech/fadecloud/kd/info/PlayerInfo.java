package com.tadahtech.fadecloud.kd.info;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.achievements.CSAchievement;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class PlayerInfo {

    private static InfoManager infoManager = KingdomDefense.getInstance().getInfoManager();

    private UUID uuid;
    private Map<TeamType, Integer> teamLevels;
    private Map<TeamType, Integer> teamWins;
    private CSTeam currentTeam;
    private List<CSKit> kits;
    private List<CSAchievement> achievements;
    private double coins;
    private Player bukkitPlayer;
    private int kills, deaths;

    /*
     * Current Game Related Information.
     */
    private boolean invisible;
    private boolean invisibleFromChance;
    private boolean teamChat = true;
    private Structure currentStructure;

    public PlayerInfo(Player player) {
        this.uuid = player.getUniqueId();
        this.teamLevels = new HashMap<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerInfo(UUID uuid) {
        this.uuid = uuid;
    }

    public int getWins(TeamType teamType) {
        return teamWins.get(teamType);
    }

    public List<CSKit> getKits() {
        return kits;
    }

    public List<CSAchievement> getAchievements() {
        return achievements;
    }

    public double getCoins() {
        return coins;
    }

    public Player getBukkitPlayer() {
        if(bukkitPlayer != null) {
            return bukkitPlayer;
        }

        return (this.bukkitPlayer = Bukkit.getPlayer(uuid));
    }

    public int getLevel(TeamType team) {
        return teamLevels.get(team);
    }

    public CSTeam getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(CSTeam currentTeam) {
        this.currentTeam = currentTeam;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisibleFromChance(boolean invisibleFromChance) {
        this.invisibleFromChance = invisibleFromChance;
    }

    public boolean isInvisibleFromChance() {
        return invisibleFromChance;
    }

    public boolean isTeamChat() {
        return teamChat;
    }

    public void setTeamChat(boolean teamChat) {
        this.teamChat = teamChat;
    }

    public void setTeamLevels(Map<TeamType, Integer> teamLevels) {
        this.teamLevels = teamLevels;
    }

    public void setTeamWins(Map<TeamType, Integer> teamWins) {
        this.teamWins = teamWins;
    }

    public void setKits(List<CSKit> kits) {
        this.kits = kits;
    }

    public void setAchievements(List<CSAchievement> achievements) {
        this.achievements = achievements;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public void sendMessage(String s) {
        getBukkitPlayer().sendMessage(s);
    }

    public void addCoins(double amount) {
        this.coins += amount;
    }

    public boolean hasEnough(double amount) {
        return this.coins >= amount;
    }

    public boolean remove(double amount) {
        double temp = this.coins - amount;
        if(temp < 0) {
            return false;
        }
        this.coins = temp;
        return true;
    }

    public Structure getCurrentStructure() {
        return currentStructure;
    }

    public void setCurrentStructure(Structure currentStructure) {
        this.currentStructure = currentStructure;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public boolean isBeta() {
        return getBukkitPlayer().hasPermission("kd.beta");
    }

    public void setLevel(TeamType type, int i) {
        teamLevels.remove(type);
        teamLevels.put(type, i);
    }
}
