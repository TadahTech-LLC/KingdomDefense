package com.tadahtech.fadecloud.kd.map;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class Island {

    private Region region;

    public Island(Region region) {
        this.region = region;
    }

    public boolean inCastle(Location location) {
        return false;
    }

    public Region getRegion() {
        return region;
    }

    public static Island load(ConfigurationSection file) {
        Location regionMin = Utils.locFromString(file.getString("region.min"));
        Location regionMax = Utils.locFromString(file.getString("region.max"));
        try {
            Location castleMin = Utils.locFromString(file.getString("castle.min"));
            Location castleMax = Utils.locFromString(file.getString("castle.max"));
        } catch (Exception e) {
            //old file, dw...
        }
        Region region = new Region(regionMin, regionMax);
        return new Island(region);
    }

    public Map<String, Object> save() {
        Map<String, Object> map = Maps.newHashMap();
        Map<String, Object> region = Maps.newHashMap();
        region.putIfAbsent("min", Utils.locToString(this.region.getMin()));
        region.putIfAbsent("max", Utils.locToString(this.region.getMax()));
        map.putIfAbsent("region", region);
        return map;
    }

}
