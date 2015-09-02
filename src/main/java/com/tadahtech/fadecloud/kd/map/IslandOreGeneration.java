package com.tadahtech.fadecloud.kd.map;

import com.tadahtech.fadecloud.kd.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class IslandOreGeneration {

    private static Random random = new Random();

    public static void generate(Island island) {
        Region region = island.getRegion();
        for (int y = region.getMinY(); y < region.getMaxY(); y++) {
            for (int x = region.getMinX(); x < region.getMaxX(); x++) {
                for (int z = region.getMinZ(); z < region.getMaxZ(); z++) {
                    Location location = new Location(Game.WORLD, x, y, z);
                    if (location.getBlock().getType() != Material.STONE) {
                        continue;
                    }
                    int next = random.nextInt(200);
                    if (next <= 1) {
                        location.getBlock().setType(Material.DIAMOND_ORE);
                        continue;
                    }
                    next = random.nextInt(200);
                    if (next <= 5) {
                        location.getBlock().setType(Material.GOLD_ORE);
                        continue;
                    }
                    next = random.nextInt(200);
                    if (next <= 10) {
                        location.getBlock().setType(Material.IRON_ORE);
                        continue;
                    }
                    next = random.nextInt(200);
                    if(next <= 25) {
                        location.getBlock().setType(Material.COAL_ORE);
                    }
                }
            }
        }
    }
}

