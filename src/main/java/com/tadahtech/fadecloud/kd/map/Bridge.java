package com.tadahtech.fadecloud.kd.map;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class Bridge {

    private Map<Location, BlockData> blocks;

    public Bridge(Location min, Location max){
        this.blocks = Maps.newHashMap();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Location location = new Location(min.getWorld(), x, y, z);
                    Block block = location.getBlock();
                    if(block.getType() == Material.AIR) {
                        continue;
                    }
                    if(block.getType() ==Material.GRASS || block.getType() == Material.DIRT || block.getType() == Material.STONE) {
                        continue;
                    }
                    blocks.putIfAbsent(location, new BlockData(block));
                    block.setType(Material.AIR);
                }
            }
        }
    }

    public Bridge(Map<Location, BlockData> blocks) {
        this.blocks = blocks;
    }

    public void place() {
        blocks.entrySet().stream().forEach(entry -> entry.getValue().set(entry.getKey()));
    }

    public String save() {
        StringBuilder builder = new StringBuilder();
        blocks.entrySet().stream().forEach(entry -> builder.append(Utils.locToString(entry.getKey())).append("=").append(entry.getValue().toString()).append("@"));
        return builder.toString();
    }

    public static Bridge load(String s) {
        String[] str = s.split("@");
        Map<Location, BlockData> map = Maps.newHashMap();
        for(String string : str) {
            String[] locAndData = string.split("=");
            if(locAndData.length != 2) {
                continue;
            }
            Location location = Utils.locFromString(locAndData[0]);
            BlockData data = BlockData.from(locAndData[1]);
            map.putIfAbsent(location, data);
        }
        return new Bridge(map);
    }

}
