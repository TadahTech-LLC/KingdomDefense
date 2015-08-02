package com.tadahtech.fadecloud.kd.map;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class Island {

    private Region region, castleRegion;
    private Map<Chunk, Structure> structures;
    private int lowest;

    public Island(Region region, Region castleRegion) {
        this.region = region;
        this.castleRegion = castleRegion;
        this.structures = new HashMap<>();
    }

    public Optional<Structure> getStructure(Location location) {
        return Optional.ofNullable(structures.get(location.getChunk()));
    }

    public boolean inCastle(Location location) {
        return castleRegion.canBuild(location);
    }

    public void addStructure(Chunk chunk, Structure structure) {
        structures.put(chunk, structure);
    }

    public Region getRegion() {
        return region;
    }

    public Region getCastleRegion() {
        return castleRegion;
    }

    public int getLowest() {
        return lowest;
    }

    public void setLowest(int lowest) {
        this.lowest = lowest;
    }

    public static Island load(ConfigurationSection file) {
        Location regionMin = Utils.locFromString(file.getString("region.min"));
        Location regionMax = Utils.locFromString(file.getString("region.max"));
        Location castleMin = Utils.locFromString(file.getString("castle.min"));
        Location castleMax = Utils.locFromString(file.getString("castle.max"));
        int lowest = file.getInt("lowest");
        Region region = new Region(regionMin, regionMax);
        Region castle = new Region(castleMin, castleMax);
        Island island = new Island(region, castle);
        island.setLowest(lowest);
        return island;
    }

    public Map<String, Object> save() {
        Map<String, Object> map = Maps.newHashMap();
        Map<String, Object> region = Maps.newHashMap();
        Map<String, Object> castle = Maps.newHashMap();
        region.putIfAbsent("min", Utils.locToString(this.region.getMin()));
        region.putIfAbsent("ma", Utils.locToString(this.region.getMax()));
        map.putIfAbsent("region", region);
        castle.putIfAbsent("min", Utils.locToString(this.castleRegion.getMax()));
        castle.putIfAbsent("max", Utils.locToString(this.castleRegion.getMax()));
        map.putIfAbsent("castle", castle);
        map.putIfAbsent("lowest", getLowest());
        return map;
    }

    public int getCount(StructureType type) {
        return structures.values().stream().filter(structure -> structure.getStructureType() == type).collect(Collectors.toList()).size();
    }

}
