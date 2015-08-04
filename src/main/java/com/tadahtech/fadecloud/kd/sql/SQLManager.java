package com.tadahtech.fadecloud.kd.sql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.achievements.CSAchievement;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Timothy Andis
 */
public class SQLManager {

    private QueryThread queryThread;
    private Connection con;
    private int query_count = 0;
    private String host, db, user, pass;
    private int port;
    private String url;

    public SQLManager(String host, String db, String user, String pass, int port) {
        KingdomDefense.getInstance().getLogger().info("Hogging the Main Thread for a second, please stand by....");
        long start = System.currentTimeMillis();
        KingdomDefense.getInstance().getLogger().info("Host: " + host + " : Password: " + pass + " : User: " + user + " : DB: " + db + " : Port:" + port);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, pass);
            PreparedStatement statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + db);
            statement.execute();
        } catch (ClassNotFoundException | SQLException e) {
            KingdomDefense.getInstance().getLogger().severe(e.getMessage());
            KingdomDefense.getInstance().getLogger().warning("Heyo! You have SQL set as the storageType, but I couldn't connect using the SQL details provided. Please edit those. Disabling...");
            KingdomDefense.getInstance().getPluginLoader().disablePlugin(KingdomDefense.getInstance());
            return;
        }
        long end = System.currentTimeMillis();
        long total = end - start;
        KingdomDefense.getInstance().getLogger().info("OK, Done with the Main Thread! Took: " + total + "ms");
        this.host = host;
        this.db = db;
        this.user = user;
        this.pass = pass;
        this.port = port;
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db;
        getConnection();
        this.queryThread = new QueryThread(this);
        queryThread.addQuery("CREATE TABLE IF NOT EXISTS `player_info`" +
          "(" +
          "`player` varchar(64) PRIMARY KEY NOT NULL, " +

          "`kills` int," +
          "`deaths` int," +

          "`creeper_wins` int," +
          "`zombie_wins` int," +
          "`skeleton_wins` int," +
          "`enderman_wins` int," +

          "`creeper_level` int," +
          "`zombie_level` int," +
          "`skeleton_level` int," +
          "`enderman_level` int," +
          
          "`kits` longtext," +
          "`coins` int," +
          "`achievements` longtext" +
          ")");
    }

    public Connection getConnection() {
        try {
            if (query_count >= 1000) {
                if (con != null) {
                    con.close();
                }
                con = DriverManager.getConnection(url, user, pass);
                query_count = 0;
            }
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(url, user, pass);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                con = DriverManager.getConnection(url, user, pass);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        query_count++;
        return con;
    }

    public ResultSet getResultSet(SQLStatement query) {
        PreparedStatement pst;
        try {
            pst = query.prepare(getConnection());
            pst.execute();
            return pst.getResultSet();
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public Callback<PlayerInfo> load(UUID uuid) {
        Callback<PlayerInfo> infoCallback = new Callback<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                SQLStatement statement = new SQLStatement("SELECT * FROM `player_info` WHERE `player` = ?");
                statement.set(1, uuid.toString());
                ResultSet res = getResultSet(statement);
                PlayerInfo info = new PlayerInfo(uuid);
                try {
                    if (res.next()) {
                        int kills = res.getInt("kills");
                        int deaths = res.getInt("deaths");
                        int coins = res.getInt("coins");
                        
                        int creeper_wins = res.getInt("creeper_wins");
                        int creeper_level = res.getInt("creeper_level");

                        int zombie_wins = res.getInt("zombie_wins");
                        int zombie_level = res.getInt("zombie_level");

                        int skeleton_wins = res.getInt("skeleton_wins");
                        int skeleton_level = res.getInt("skeleton_level");
                        
                        int enderman_wins = res.getInt("enderman_wins");
                        int enderman_level = res.getInt("enderman_level");

                        String kitRaw = res.getString("kits");
                        List<String> split = Utils.split(kitRaw, ",");
                        List<CSKit> kits = split.stream().map(CSKit::from).collect(Collectors.toList());
                        List<CSAchievement> achievements = Lists.newArrayList();

                        info.setKills(kills);
                        info.setDeaths(deaths);

                        info.setAchievements(achievements);
                        info.setCoins(coins);
                        info.setKits(kits);
                        info.setTeamChat(true);

                        Map<TeamType, Integer> teamWins = Maps.newHashMap();
                        Map<TeamType, Integer> teamLevels = Maps.newHashMap();

                        teamLevels.putIfAbsent(TeamType.CREEPER, creeper_level == 0 ? 1 : creeper_level);
                        teamWins.putIfAbsent(TeamType.CREEPER, creeper_wins);

                        teamLevels.putIfAbsent(TeamType.ZOMBIE, zombie_level == 0 ? 1 : zombie_level);
                        teamWins.putIfAbsent(TeamType.ZOMBIE, zombie_wins);

                        teamLevels.putIfAbsent(TeamType.SKELETON, skeleton_level == 0 ? 1 : skeleton_level);
                        teamWins.putIfAbsent(TeamType.SKELETON, skeleton_wins);

                        teamLevels.putIfAbsent(TeamType.ENDERMAN, enderman_level == 0 ? 1 : enderman_level);
                        teamWins.putIfAbsent(TeamType.ENDERMAN, enderman_wins);

                        info.setTeamLevels(teamLevels);
                        info.setTeamWins(teamWins);
                    } else {
                        List<CSAchievement> achievements = Lists.newArrayList();

                        info.setKills(0);
                        info.setDeaths(0);

                        info.setAchievements(achievements);
                        info.setCoins(0);
                        info.setKits(Lists.newArrayList());
                        info.setTeamChat(true);

                        Map<TeamType, Integer> teamWins = Maps.newHashMap();
                        Map<TeamType, Integer> teamLevels = Maps.newHashMap();

                        teamLevels.putIfAbsent(TeamType.CREEPER, 1);
                        teamWins.putIfAbsent(TeamType.CREEPER, 0);

                        teamLevels.putIfAbsent(TeamType.ZOMBIE, 1);
                        teamWins.putIfAbsent(TeamType.ZOMBIE, 0);

                        teamLevels.putIfAbsent(TeamType.SKELETON, 1);
                        teamWins.putIfAbsent(TeamType.SKELETON, 0);

                        teamLevels.putIfAbsent(TeamType.ENDERMAN, 1);
                        teamWins.putIfAbsent(TeamType.ENDERMAN, 0);

                        info.setTeamLevels(teamLevels);
                        info.setTeamWins(teamWins);
                    }
                    infoCallback.call(info);
                    KingdomDefense.getInstance().getInfoManager().put(info);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(KingdomDefense.getInstance());
        return infoCallback;
    }

    public void save(PlayerInfo info) {
        String base = "INSERT INTO `player_info` VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
          " ON DUPLICATE KEY UPDATE `kills` = ?, `deaths` = ?, " +
          "`creeper_wins` = ?, `creeper_level` = ?, " +
          "`zombie_wins` = ?, `zombie_level` = ?, " +
          "`skeleton_wins` = ?, `skeleton_level` = ?" +
          "`enderman_wins` = ?, `enderman_level` = ?" +
          "`kits` = ?, `coins` = ?, `achievements` = ?"
          ;

        SQLStatement statement = new SQLStatement(base);
        statement.set(1, info.getUuid().toString());
        statement.set(2, info.getKills());
        statement.set(3, info.getDeaths());
        statement.set(4, info.getWins(TeamType.CREEPER));
        statement.set(5, info.getWins(TeamType.ZOMBIE));
        statement.set(6, info.getWins(TeamType.SKELETON));
        statement.set(7, info.getWins(TeamType.ENDERMAN));
        statement.set(8, info.getLevel(TeamType.CREEPER));
        statement.set(9, info.getLevel(TeamType.ZOMBIE));
        statement.set(10, info.getLevel(TeamType.SKELETON));
        statement.set(11, info.getLevel(TeamType.ENDERMAN));
        statement.set(12, Utils.toString(info.getKits().stream().map(CSKit::getName).collect(Collectors.toList())));
        statement.set(13, info.getCoins());
        statement.set(14, "tbd");
        statement.set(15, info.getKills());
        statement.set(16, info.getDeaths());
        statement.set(17, info.getWins(TeamType.CREEPER));
        statement.set(18, info.getLevel(TeamType.CREEPER));
        statement.set(19, info.getWins(TeamType.ZOMBIE));
        statement.set(20, info.getLevel(TeamType.ZOMBIE));
        statement.set(21, info.getWins(TeamType.SKELETON));
        statement.set(22, info.getLevel(TeamType.SKELETON));
        statement.set(23, info.getWins(TeamType.ENDERMAN));
        statement.set(24, info.getLevel(TeamType.ENDERMAN));
        statement.set(25, info.getKits().stream().map(CSKit::getName).collect(Collectors.toList()));
        statement.set(26, info.getCoins());
        statement.set(27, "tbd");
        queryThread.addQuery(statement);
    }
}
