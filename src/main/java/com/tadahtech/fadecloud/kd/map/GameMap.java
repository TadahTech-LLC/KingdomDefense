package com.tadahtech.fadecloud.kd.map;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class GameMap {

    private static Map<String, GameMap> maps = new HashMap<>();

    private Map<LocationType, Location> locations;
    private Map<TeamType, Island> islands;
    private String[] authors;
    private String name;
    private int min, max;
    private Bridge bridge;

    public GameMap(Map<LocationType, Location> locations, String[] authors, String name, int min, int max, Map<TeamType, Island> islands, Bridge bridge) {
        this.locations = locations;
        this.authors = authors;
        this.name = name;
        this.min = min;
        this.max = max;
        this.islands = islands;
        this.bridge = bridge;
        maps.putIfAbsent(name, this);
    }

    public static Collection<GameMap> getAll() {
        return maps.values();
    }

    public static Optional<GameMap> get(String name) {
        return Optional.ofNullable(maps.get(name));
    }

    public Optional<Location> getLocation(LocationType type) {
        return Optional.ofNullable(locations.get(type));
    }

    public Map<LocationType, Location> getLocations() {
        return locations;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getName() {
        return name;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public static GameMap load(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("locations");
        Map<LocationType, Location> locations = Maps.newHashMap();
        for (String s : section.getKeys(false)) {
            LocationType type = LocationType.valueOf(s.toUpperCase());
            Location location = Utils.locFromString(section.getString(s));
            locations.put(type, location);
        }
        Map<TeamType, Island> islandMap = Maps.newHashMap();
        String name = file.getName().replace(".yml", "");
        int min = config.getInt("min");
        int max = config.getInt("max");
        String[] authors = config.getStringList("authors").toArray(new String[config.getStringList("authors").size()]);
        ConfigurationSection islands = config.getConfigurationSection("islands");
        for (String isl : islands.getKeys(false)) {
            TeamType teamType = TeamType.valueOf(isl);
            ConfigurationSection sec = islands.getConfigurationSection(isl);
            islandMap.putIfAbsent(teamType, Island.load(sec));
        }
        Bridge bridge = Bridge.load(config.getConfigurationSection("bridge"));
        if (bridge != null) {
            bridge.clear();
        }
        return new GameMap(locations, authors, name, min, max, islandMap, bridge);
    }

    public Map<String, Object> save() {
        Map<String, Object> map = Maps.newHashMap();
        Map<String, Object> locations = Maps.newHashMap();
        Map<String, Object> islands = Maps.newHashMap();
        map.putIfAbsent("min", min);
        map.putIfAbsent("max", max);
        map.putIfAbsent("authors", new ArrayList<>(Arrays.asList(authors)));
        for (Entry<LocationType, Location> entry : this.locations.entrySet()) {
            locations.putIfAbsent(entry.getKey().name(), Utils.locToString(entry.getValue()));
        }
        map.putIfAbsent("locations", locations);
        for (Entry<TeamType, Island> entry : this.islands.entrySet()) {
            islands.putIfAbsent(entry.getKey().name(), entry.getValue().save());
        }
        map.putIfAbsent("islands", islands);
        if (bridge != null) {
            map.putIfAbsent("bridge", this.bridge.save());
        }
        return map;
    }

    public Map<TeamType, Island> getIslands() {
        return islands;
    }

    public void dropBridge() {
        bridge.place();
    }

    public Location getRespawn(TeamType type) {
        switch (type) {
            case CREEPER:
                return getLocation(LocationType.CREEPER_SPAWN).get();
            case ENDERMAN:
                return getLocation(LocationType.ENDERMAN_SPAWN).get();
            case SKELETON:
                return getLocation(LocationType.SKELETON_SPAWN).get();
            case ZOMBIE:
                return getLocation(LocationType.ZOMBIE_SPAWN).get();
        }
        return null;
    }

    public Bridge getBridge() {
        return bridge;
    }

    public void setBridge(Bridge bridge) {
        this.bridge = bridge;
    }

    public void removeBridge() {
        this.bridge.clear();
    }

    //Unloading maps, to rollback maps. Will delete all player builds until last server save
    public void unloadMap(){
        if(Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(name), false)){
            KingdomDefense.getInstance().getLogger().info("Successfully unloaded " + name);
        }else{
            KingdomDefense.getInstance().getLogger().severe("COULD NOT UNLOAD " + name);
        }
    }

    //Loading maps (MUST BE CALLED AFTER UNLOAD MAPS TO FINISH THE ROLLBACK PROCESS)
    public void loadMap(){
        Bukkit.getServer().createWorld(new WorldCreator(getName()));
    }

    //Maprollback method, because were too lazy to type 2 lines
    public void rollback(){
        unloadMap();
        loadMap();
    }
}
