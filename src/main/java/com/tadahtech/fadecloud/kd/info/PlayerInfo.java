package com.tadahtech.fadecloud.kd.info;

import ca.wacos.nametagedit.NametagAPI;
import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.achievements.CSAchievement;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.party.Party;
import com.tadahtech.fadecloud.kd.scoreboard.Lobbyboard;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class PlayerInfo {

    private static InfoManager infoManager = KingdomDefense.getInstance().getInfoManager();

    private boolean requested = false;

    private UUID uuid;
    private Map<TeamType, Integer> teamLevels;
    private Map<TeamType, Integer> teamWins;
    private CSTeam currentTeam;
    private List<CSKit> kits;
    private List<CSAchievement> achievements;
    private double coins;
    private Player bukkitPlayer;
    private int kills, deaths, blocksBroken;

    /*
     * Current Game Related Information.
     */
    private boolean invisible = false;
    private boolean invisibleFromChance = false;
    private boolean teamChat = false;
    private Structure currentStructure;
    private Party party;
    private Party invitedTo;
    private boolean invincible, beenTold;
    private int foundChests;
    private CSKit activeKit;

    public PlayerInfo(Player player) {
        this.uuid = player.getUniqueId();
        this.teamLevels = new HashMap<TeamType, Integer>() {{
            put(TeamType.CREEPER, 1);
            put(TeamType.ZOMBIE, 1);
            put(TeamType.SKELETON, 1);
            put(TeamType.ENDERMAN, 1);
        }};
        this.teamWins = new HashMap<TeamType, Integer>() {{
            put(TeamType.CREEPER, 0);
            put(TeamType.ZOMBIE, 0);
            put(TeamType.SKELETON, 0);
            put(TeamType.ENDERMAN, 0);
        }};
        this.coins = 250;
        this.kills = 0;
        this.deaths = 0;
        this.kits = Lists.newArrayList();
        this.achievements = Lists.newArrayList();
        infoManager.put(this);
    }

    public PlayerInfo(UUID uuid, double coins, int kills, int deaths, List<CSKit> kits, int creeper_level, int creeper_wins, int zombie_level, int zombie_wins, int enderman_level, int enderman_wins, int skeleton_level, int skeleton_wins) {
        this.uuid = uuid;
        this.coins = coins;
        this.kills = kills;
        this.deaths = deaths;
        this.kits = kits;
        this.achievements = Lists.newArrayList();
        this.teamLevels = new HashMap<TeamType, Integer>() {{
            put(TeamType.CREEPER, creeper_level);
            put(TeamType.ZOMBIE, zombie_level);
            put(TeamType.SKELETON, skeleton_level);
            put(TeamType.ENDERMAN, enderman_level);
        }};
        this.teamWins = new HashMap<TeamType, Integer>() {{
            put(TeamType.CREEPER, creeper_wins);
            put(TeamType.ZOMBIE, zombie_wins);
            put(TeamType.SKELETON, skeleton_wins);
            put(TeamType.ENDERMAN, enderman_wins);
        }};
        infoManager.put(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerInfo(UUID uuid) {
        this.uuid = uuid;
        this.teamLevels = new HashMap<TeamType, Integer>() {{
            put(TeamType.CREEPER, 1);
            put(TeamType.ZOMBIE, 1);
            put(TeamType.SKELETON, 1);
            put(TeamType.ENDERMAN, 1);
        }};
        this.teamWins = new HashMap<TeamType, Integer>() {{
            put(TeamType.CREEPER, 0);
            put(TeamType.ZOMBIE, 0);
            put(TeamType.SKELETON, 0);
            put(TeamType.ENDERMAN, 0);
        }};
        this.coins = 250;
        this.kills = 0;
        this.deaths = 0;
        this.kits = Lists.newArrayList();
        this.achievements = Lists.newArrayList();
        infoManager.put(this);
    }

    public int getWins(TeamType teamType) {
        if (this.teamWins == null) {
            this.teamWins = new HashMap<TeamType, Integer>() {{
                put(TeamType.CREEPER, 0);
                put(TeamType.ZOMBIE, 0);
                put(TeamType.SKELETON, 0);
                put(TeamType.ENDERMAN, 0);
            }};
            return 0;
        }
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
        return Bukkit.getPlayer(uuid);
    }

    public int getLevel(TeamType team) {
        return teamLevels.get(team) == null ? 1 : teamLevels.get(team);
    }

    public CSTeam getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(CSTeam currentTeam) {
        this.currentTeam = currentTeam;
        Player player = getBukkitPlayer();
        String name = player.getName();
        if(name.length() > 14) {
            name = name.substring(0, 14);
        }
        switch (currentTeam.getType()) {
            case CREEPER:
                NametagAPI.setNametagHard(player.getName(), ChatColor.GREEN + "[Creeper] ", "");
                player.setPlayerListName(ChatColor.GREEN + name);
                break;
            case ZOMBIE:
                NametagAPI.setNametagHard(player.getName(), ChatColor.DARK_GREEN + "[Zombie] ", "");
                player.setPlayerListName(ChatColor.DARK_GREEN + name);
                break;
            case SKELETON:
                NametagAPI.setNametagHard(player.getName(), ChatColor.GRAY + "[Skeleton] ", "");
                player.setPlayerListName(ChatColor.GRAY + name);
                break;
            case ENDERMAN:
                NametagAPI.setNametagHard(player.getName(), ChatColor.LIGHT_PURPLE + "[Enderman] ", "");
                player.setPlayerListName(ChatColor.LIGHT_PURPLE + name);
                break;
        }
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
        if(invisible) {
            ItemStack[] armor = bukkitPlayer.getInventory().getArmorContents();
            bukkitPlayer.getInventory().setArmorContents(null);
            new BukkitRunnable() {
                @Override
                public void run() {
                    bukkitPlayer.getInventory().setArmorContents(armor);
                    bukkitPlayer.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }.runTaskLater(KingdomDefense.getInstance(), 20L * 8);
        }
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisibleFromChance(boolean invisibleFromChance) {
        this.invisibleFromChance = invisibleFromChance;
        if(invisibleFromChance) {
            ItemStack[] armor = bukkitPlayer.getInventory().getArmorContents();
            bukkitPlayer.getInventory().setArmorContents(null);
            new BukkitRunnable() {
                @Override
                public void run() {
                    bukkitPlayer.getInventory().setArmorContents(armor);
                    bukkitPlayer.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }.runTaskLater(KingdomDefense.getInstance(), 20L * 8);
        }
    }

    public boolean isInvisibleFromChance() {
        return invisibleFromChance;
    }

    public boolean isTeamChat() {
        return getCurrentTeam() != null && teamChat;
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
        Game game = KingdomDefense.getInstance().getGame();
        if (game == null) {
            Lobbyboard lobbyboard = KingdomDefense.getInstance().getLobbyboard();
            if (lobbyboard != null) {
                lobbyboard.flip();
            }
            return;
        }
        game.flip();
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
        setCoins(this.coins - amount);
        return true;
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

    public void setWins(TeamType creeper, int creeper_wins) {
        this.teamWins.put(creeper, creeper_wins);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(uuid.toString());
        builder.append(":");
        builder.append(this.getCoins());
        builder.append(":");
        builder.append(this.getKills());
        builder.append(":");
        builder.append(this.getDeaths());
        builder.append(":");
        builder.append(Utils.toString(this.getKits().stream().map(CSKit::getName).collect(Collectors.toList())));
        builder.append(":");
        //Levels
        builder.append(this.getLevel(TeamType.CREEPER));
        builder.append(":");
        builder.append(this.getLevel(TeamType.ZOMBIE));
        builder.append(":");
        builder.append(this.getLevel(TeamType.ENDERMAN));
        builder.append(":");
        builder.append(this.getLevel(TeamType.SKELETON));
        builder.append(":");
        //Wins
        builder.append(this.getWins(TeamType.CREEPER));
        builder.append(":");
        builder.append(this.getWins(TeamType.ZOMBIE));
        builder.append(":");
        builder.append(this.getWins(TeamType.ENDERMAN));
        builder.append(":");
        builder.append(this.getWins(TeamType.SKELETON));
        return (builder.toString());
    }

    public static PlayerInfo fromString(String message) {
        String[] str = message.split(":");
        UUID uuid = UUID.fromString(str[0]);
        double coins = Double.parseDouble(str[1]);
        int kills = Utils.parse(str[2]);
        int deaths = Utils.parse(str[3]);
        List<String> split = Utils.split(str[4], ",");
        List<CSKit> kits = split.stream().map(CSKit::from).collect(Collectors.toList());
        int creeper_level = Utils.parse(str[5]);
        int zombie_level = Utils.parse(str[6]);
        int enderman_level = Utils.parse(str[7]);
        int skeleton_level = Utils.parse(str[8]);
        int creeper_wins = Utils.parse(str[9]);
        int zombie_wins = Utils.parse(str[10]);
        int enderman_wins = Utils.parse(str[11]);
        int skeleton_wins = Utils.parse(str[12]);
        return new PlayerInfo(uuid, coins, kills, deaths, kits,
          creeper_level, creeper_wins, zombie_level, zombie_wins, enderman_level, enderman_wins, skeleton_level, skeleton_wins);

    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setInvitedTo(Party invitedTo) {
        this.invitedTo = invitedTo;
    }

    public Party getInvitedTo() {
        return invitedTo;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void addWin(TeamType type) {
        setWins(type, (getWins(type) + 1));
    }

    public boolean hasBeenTold() {
        return beenTold;
    }

    public void setBeenTold(boolean beenTold) {
        this.beenTold = beenTold;
        if(beenTold) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    setBeenTold(false);
                }
            }.runTaskLater(KingdomDefense.getInstance(), 20L * 30);
        }
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public int getFoundChests() {
        return foundChests;
    }

    public void setFoundChests(int foundChests) {
        this.foundChests = foundChests;
    }

    public CSKit getActiveKit() {
        return activeKit;
    }

    public void setActiveKit(CSKit activeKit) {
        this.activeKit = activeKit;
    }
}
