package com.tadahtech.fadecloud.kd.map.structures;

import com.sk89q.worldedit.Vector;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.map.Island;
import org.bukkit.Location;

public class GridLocation {

    private final int x;
    private final int z;

    public GridLocation() {
        this.x = 0;
        this.z = 0;
    }

    public GridLocation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public static GridLocation fromWorldLocation(Island island, Location loc) {
        return fromWorldVector(island, new Vector(loc.getBlockX(), 0, loc.getBlockZ()));
    }

    public static GridLocation fromWorldVector(Island island, Vector vec) {
        Vector adjusted = vec.subtract(island.getMin()).divide(16).floor();
        return new GridLocation(adjusted.getBlockX(), adjusted.getBlockZ());
    }

    public Vector toWorldVector(Island island) {
        return new Vector(x, 0, z).multiply(16).add(island.getMin());
    }

    public Location toWorldLocation(Island island) {
        Vector vec = toWorldVector(island);
        return new Location(Game.WORLD, vec.getBlockX(), 0, vec.getBlockZ(), 0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GridLocation that = (GridLocation) o;
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        return result;
    }
}
