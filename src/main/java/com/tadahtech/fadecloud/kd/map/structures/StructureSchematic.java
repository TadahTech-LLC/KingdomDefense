package com.tadahtech.fadecloud.kd.map.structures;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
@SuppressWarnings("ALL")
public class StructureSchematic {

    private static final Vector VECTOR_0_0_0 = new Vector(0, 0, 0);

    private final CuboidClipboard clipboard;
    private final int centerX;
    private final int centerZ;
    private final int level;

    public StructureSchematic(CuboidClipboard clipboard, int level) {
        this.clipboard = clipboard;
        this.level = level;

        int lowestX = Integer.MAX_VALUE;
        int highestX = Integer.MIN_VALUE;
        int lowestZ = Integer.MAX_VALUE;
        int highestZ = Integer.MIN_VALUE;
        for (int x = 0; x < clipboard.getWidth(); x++) {
            for (int z = 0; z < clipboard.getLength(); z++) {
                for (int y = 0; y < clipboard.getHeight(); y++) {
                    boolean found = clipboard.getBlock(new Vector(x, y, z)).getType() != 0;
                    if (found) {
                        if (x < lowestX) {
                            lowestX = x;
                        } else if (x > highestX) {
                            highestX = x;
                        }
                        if (z < lowestZ) {
                            lowestZ = z;
                        } else if (z > highestZ) {
                            highestZ = z;
                        }
                        break;
                    }
                }
            }
        }

        this.centerX = (highestX - lowestX) / 2;
        this.centerZ = (highestZ - lowestZ) / 2;
    }

    public int getLevel() {
        return level;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public CuboidClipboard getClipboard() {
        // create a copy
        CuboidClipboard newClipboard = new CuboidClipboard(clipboard.getSize(), clipboard.getOrigin(), clipboard.getOffset());
        for (int x = 0; x < clipboard.getWidth(); x++) {
            for (int z = 0; z < clipboard.getLength(); z++) {
                for (int y = 0; y < clipboard.getHeight(); y++) {
                    Vector vector = new Vector(x, y, z);
                    newClipboard.setBlock(vector, clipboard.getBlock(vector));
                }
            }
        }
        newClipboard.setOffset(new Vector(-centerX, 0, -centerZ));
        newClipboard.setOrigin(VECTOR_0_0_0);
        return newClipboard;
    }

    public boolean isBlockAt(Vector vector) {
        BaseBlock blockData;
        try {
            blockData = clipboard.getBlock(vector);
        } catch (Exception e) {
            return false;
        }
        if (blockData == null) {
            return false;
        }
        switch (blockData.getType()) {
            case 0: // AIR
                //case 2: // GRASS
                //case 3: // DIRT
                return false;
        }
        return true;
    }

    public boolean isBlock(Block block) {
        if (block == null) {
            return false;
        }
        switch (block.getType().getId()) {
            case 0:
                //case 2: // GRASS
                //case 3: // DIRT
                return false;
        }
        return true;
    }

    public boolean isNormalBlockAt(Block block, Vector vector) {
        BaseBlock baseBlock;
        try {
            baseBlock = clipboard.getBlock(vector);
        } catch (ArrayIndexOutOfBoundsException e) {
            return true; // is not in schematic
        }
        boolean isBlock = isBlockAt(vector);
        if (baseBlock == null) {
            return !isBlock || block.getType() == Material.AIR;
        } else if (!isBlock) {
            return true;
        }
        return baseBlock.getType() == block.getTypeId();
    }
}
