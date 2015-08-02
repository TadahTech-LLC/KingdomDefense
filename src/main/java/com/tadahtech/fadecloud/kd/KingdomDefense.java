package com.tadahtech.fadecloud.kd;

import com.tadahtech.fadecloud.kd.commands.CommandHandler;
import com.tadahtech.fadecloud.kd.commands.sub.CreateCommand;
import com.tadahtech.fadecloud.kd.commands.sub.StatsCommand;
import com.tadahtech.fadecloud.kd.commands.sub.TestCommand;
import com.tadahtech.fadecloud.kd.csc.JedisManager;
import com.tadahtech.fadecloud.kd.csc.ServerTeleporter;
import com.tadahtech.fadecloud.kd.csc.packets.ServerInitPacket;
import com.tadahtech.fadecloud.kd.csc.serverComm.BungeeServerTeleporter;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.info.InfoManager;
import com.tadahtech.fadecloud.kd.io.KitIO;
import com.tadahtech.fadecloud.kd.io.MapIO;
import com.tadahtech.fadecloud.kd.io.SignIO;
import com.tadahtech.fadecloud.kd.listeners.*;
import com.tadahtech.fadecloud.kd.map.GameMap;
import com.tadahtech.fadecloud.kd.menu.MenuListener;
import com.tadahtech.fadecloud.kd.sql.SQLManager;
import com.tadahtech.fadecloud.kd.threads.ai.FollowingThread;
import com.tadahtech.fadecloud.kd.threads.ai.TargetingThread;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class KingdomDefense extends JavaPlugin {

    private static KingdomDefense instance;
    private JedisManager jedisManager;
    private ServerTeleporter serverTeleporter;
    private Game game;
    private SignIO signIO;
    private KitIO kitIO;
    private MapIO mapIO;
    //UI-Name -> Server name
    private Map<String, String> serverNames;
    private GameMap map;
    private InfoManager infoManager;
    private SQLManager sqlManager;

    public static KingdomDefense getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.serverNames = new HashMap<>();
        saveDefaultConfig();
        new ServerInitPacket().write();
        //Register Game / Hub Listeners
        if(!this.getHubServerName().equalsIgnoreCase(this.getServerName())) {
            getServer().getPluginManager().registerEvents(new GameListener(), this);
            getServer().getPluginManager().registerEvents(new TeamListener(), this);
            getServer().getPluginManager().registerEvents(new BlockListener(), this);
            getServer().getPluginManager().registerEvents(new EntityListener(), this);
            new TargetingThread();
            new FollowingThread();
            this.game = new Game();
            this.mapIO = new MapIO();
            this.kitIO = new KitIO(this);
        } else {
            getServer().getPluginManager().registerEvents(new SignListener(), this);
        }
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        getServer().getPluginManager().registerEvents(new InfoListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        Game.WORLD = getServer().getWorld(getConfig().getString("world"));
        this.jedisManager = new JedisManager(getConfig());
        this.serverTeleporter = new BungeeServerTeleporter();
        this.signIO = new SignIO();
        FileConfiguration config = getConfig();
        String host = config.getString("sql.host");
        String pass = config.getString("sql.pass");
        String db = config.getString("sql.db");
        String user = config.getString("sql.user");
        int port = config.getInt("sql.port");
        this.sqlManager = new SQLManager(host, db, user, pass, port);
        this.infoManager = new InfoManager(sqlManager);
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.register(new TestCommand());
        commandHandler.register(new CreateCommand());
        commandHandler.register(new StatsCommand());
    }

    @Override
    public void onDisable() {
        this.signIO.save();
        this.mapIO.save();
    }

    public JedisManager getJedisManager() {
        return jedisManager;
    }

    public void redirect(String server, Player player) {
        serverTeleporter.send(player, server);
    }

    public String getServerName() {
        return getConfig().getString("server-name");
    }

    public String getUIName() {
        return getConfig().getString("ui-name");
    }

    public String getHubServerName() {
        return getConfig().getString("hub-server-name");
    }

    public Game getGame() {
        return game;
    }

    public Map<String, String> getServerNames() {
        return serverNames;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public InfoManager getInfoManager() {
        return infoManager;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public MapIO getMapIO() {
        return mapIO;
    }
}
