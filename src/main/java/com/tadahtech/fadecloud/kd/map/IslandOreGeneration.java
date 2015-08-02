package com.tadahtech.fadecloud.kd.map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Random;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class IslandOreGeneration {

    private static Random random = new Random();

    public static void generate(Island island) {
        int diamondTotal = random.nextInt(20) + 12;
        int ironTotal = random.nextInt(25) + 25;
        int goldTotal = random.nextInt(4) + 12;
        Region region = island.getRegion();
        Location min = region.getMin();
        Location max = region.getMax();
        Location clone = max.clone();
        clone.setY(island.getLowest());
        for (int y = clone.getBlockY() - 4; y < clone.getBlockY(); y++) {
            for (int x = clone.getBlockX() - 4; x < clone.getBlockX(); x++) {
                for (int z = clone.getBlockZ() - 4; z < clone.getBlockZ(); z++) {
                    Location location = new Location(clone.getWorld(), x, y, z);
                    if(location.getBlock().getType() == Material.AIR) {
                        continue;
                    }
                    int next = random.nextInt(100);
                    if(next <= 50) {
                        location.getBlock().setType(Material.COAL_ORE);
                        for(int i = 0; i < random.nextInt(10) + 5; i++) {
                            double addX = (random.nextBoolean() ? random.nextDouble() + i : -random.nextDouble() - 1);
                            double addZ = (random.nextBoolean() ? random.nextDouble() + i : -random.nextDouble() - i);
                            double addY = (random.nextBoolean() ? random.nextDouble() : -random.nextDouble());
                            location.clone().add(addX, addY, addZ).getBlock().setType(Material.COAL_ORE);
                        }
                    }
                }
            }
        }
        for (int y = min.getBlockY(); y < clone.getBlockY() - random.nextInt(3) + 1; y++) {
            for (int x = min.getBlockX(); x < clone.getBlockX(); x++) {
                for(int z = min.getBlockZ(); z < clone.getBlockZ(); z++) {
                    Location location = new Location(clone.getWorld(), x, y, z);
                    if(location.getBlock().getType() == Material.AIR) {
                        continue;
                    }
                    int next = random.nextInt(100);
                    if(next == 1 && diamondTotal > 0) {
                        diamondTotal--;
                        location.getBlock().setType(Material.DIAMOND_ORE);
                        if(random.nextInt(100) <= 5) {
                            location.getBlock().getRelative(BlockFace.EAST).setType(Material.DIAMOND_ORE);
                            location.getBlock().getRelative(BlockFace.DOWN).setType(Material.DIAMOND_ORE);
                            location.getBlock().getRelative(BlockFace.WEST).setType(Material.DIAMOND_ORE);
                            diamondTotal -= 3;
                        }
                        continue;
                    }
                    if(next > 1 && next <= 10 && goldTotal > 0) {
                        goldTotal--;
                        location.getBlock().setType(Material.GOLD_ORE);
                        if(random.nextInt(100) <= 5) {
                            location.getBlock().getRelative(BlockFace.WEST).setType(Material.GOLD_ORE);
                            location.getBlock().getRelative(BlockFace.SOUTH).setType(Material.GOLD_ORE);
                            goldTotal -= 2;
                        }
                        continue;
                    }
                    if(next > 10 && next <= 30 && ironTotal > 0) {
                        ironTotal -= 3;
                        location.getBlock().setType(Material.IRON_ORE);
                        location.getBlock().getRelative(BlockFace.SOUTH).setType(Material.IRON_ORE);
                        location.getBlock().getRelative(BlockFace.NORTH).setType(Material.IRON_ORE);
                        if(random.nextInt(100) <= 25) {
                            location.getBlock().getRelative(BlockFace.WEST).setType(Material.IRON_ORE);
                            location.getBlock().getRelative(BlockFace.EAST).setType(Material.IRON_ORE);
                            location.getBlock().getRelative(BlockFace.DOWN).setType(Material.IRON_ORE);
                            ironTotal -= 3;
                        }
                    }
                }
            }
        }

    }
}
