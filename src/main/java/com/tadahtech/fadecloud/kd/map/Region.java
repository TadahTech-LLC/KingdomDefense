package com.tadahtech.fadecloud.kd.map;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class Region {

	private Location min, max;
	private int minX, maxX, minY, maxY, minZ, maxZ;

	private static int ID = 0;
	private static Map<Integer, Region> regions = new HashMap<>();
	private int id;

	public Region(Location min, Location max) {
		this.min = min;
		this.max = max;
		this.minX = min.getBlockX();
		this.maxX = max.getBlockX();
		this.minY = min.getBlockY();
		this.maxY = max.getBlockY();
		this.minZ = min.getBlockZ();
		this.maxZ = max.getBlockZ();
		this.id = ++ID;
		resize();
		regions.putIfAbsent(id, this);
	}

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public static Region get(int id) {
		return regions.get(id);
	}

	public static Region get(Location location) {
		for(Region region : regions.values()) {
			if(region.canBuild(location)) {
				return region;
			}
		}
		return null;
	}

	public static Region[] getAllRegions() {
		return regions.values().toArray(new Region[regions.size()]);
	}

	public Location getMin() {
		return min;
	}

	public Location getMax() {
		return max;
	}

	public boolean canBuild(Location block) {
		int minX = (this.minX);
		int minZ = (this.minZ);
		int minY = (this.minY);
		int maxX = (this.maxX);
		int maxY = (this.maxY);
		int maxZ = (this.maxZ);
		int x = (block.getBlockX());
		int y = (block.getBlockY());
		int z = (block.getBlockZ());/*
		System.out.println("============================================");
		System.out.println("ID: " + getId() + "\nMin: " + minX + ", "  + minY + ", " + minZ + ", Max: " + maxX + ", "  + maxY + ", " + maxZ + ", Block: " + x + ", " + y + ", " + z);
		System.out.println("X: " + String.valueOf(x >= minX && x <= maxX));
		System.out.println("Y: " + String.valueOf(y <= maxY && y >= minY));
		System.out.println("Z: " + String.valueOf(z <= maxZ && z >= minZ));
		System.out.println("============================================");*/
		return (z <= maxZ && z >= minZ) && (x >= minX && x <= maxX) && (y <= maxY && y >= minY);
	}

	public void resize() {
		int minX = (this.minX);
		int maxX = (this.maxX);
		int minZ = (this.minZ);
		int maxZ = (this.maxZ);
		if(minX > maxX) {
			this.maxX = minX;
			this.minX = maxX;
		}
		if(minZ > maxZ) {
			this.maxZ = minZ;
			this.minZ = maxZ;
		}
	}

	public int getId() {
		return id;
	}

	public static void clear() {
		regions.clear();
	}

	public void setMin(Location min) {
		this.min = min;
        resize();
		this.minX = min.getBlockX();
		this.maxX = max.getBlockX();
		this.minY = min.getBlockY();
		this.maxY = max.getBlockY();
		this.minZ = min.getBlockZ();
		this.maxZ = max.getBlockZ();
	}

	public void setMax(Location max) {
		this.max = max;
        resize();
		this.minX = min.getBlockX();
		this.maxX = max.getBlockX();
		this.minY = min.getBlockY();
		this.maxY = max.getBlockY();
		this.minZ = min.getBlockZ();
		this.maxZ = max.getBlockZ();
	}

	public Map<String, Object> save() {
		Map<String, Object> map = Maps.newHashMap();
        map.put("min", Utils.locToString(min));
        map.put("max", Utils.locToString(max));
		return map;
	}
}
