package com.tadahtech.fadecloud.kd;

import com.tadahtech.fadecloud.kd.commands.CommandHandler;
import com.tadahtech.fadecloud.kd.commands.sub.*;
import com.tadahtech.fadecloud.kd.csc.JedisManager;
import com.tadahtech.fadecloud.kd.csc.ServerTeleporter;
import com.tadahtech.fadecloud.kd.csc.packets.ServerInitPacket;
import com.tadahtech.fadecloud.kd.csc.packets.request.GameInfoRequestPacket;
import com.tadahtech.fadecloud.kd.csc.packets.request.JoinGameRequestPacket;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;
import com.tadahtech.fadecloud.kd.csc.packets.response.JoinGameResponsePacket;
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
    public static boolean EDIT_MODE = false;
    private CommandHandler commandHandler;

    public static KingdomDefense getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.serverNames = new HashMap<>();
        this.saveResource("kits/Default.yml", false);
        this.saveResource("kits/Example.yml", false);
        saveDefaultConfig();
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
        new GameInfoRequestPacket();
        new GameInfoResponsePacket();
        new JoinGameRequestPacket();
        new JoinGameResponsePacket();
        //Register Game / Hub Listeners
        getLogger().info("Server-Name: " + getServerName() + " :: Hub Server Name: " + getHubServerName());
        if(!this.getHubServerName().equalsIgnoreCase(this.getServerName())) {
            new TargetingThread();
            new FollowingThread();
            Game.WORLD = getServer().getWorld(getConfig().getString("world"));
            this.mapIO = new MapIO();
            if(getMap() == null) {
                getLogger().warning("No map setup!");
            } else {
                this.game = new Game();
                getServer().getPluginManager().registerEvents(new GameListener(), this);
                getServer().getPluginManager().registerEvents(new TeamListener(), this);
                getServer().getPluginManager().registerEvents(new BlockListener(), this);
                getServer().getPluginManager().registerEvents(new EntityListener(), this);
            }

            this.kitIO = new KitIO(this);
        } else {
            getServer().getPluginManager().registerEvents(new SignListener(), this);
        }
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        getServer().getPluginManager().registerEvents(new InfoListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        this.commandHandler = new CommandHandler();
        commandHandler.register(new KDHelpCommand());
        commandHandler.register(new CreateCommand());
        commandHandler.register(new StatsCommand());
        commandHandler.register(new EditModeCommand());
        commandHandler.register(new ChatCommand());
        commandHandler.register(new LocationCommand());
        new ServerInitPacket().write();
    }

    @Override
    public void onDisable() {
        if(this.signIO != null && this.mapIO != null) {
            this.signIO.save();
            this.mapIO.save();
        }
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

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
