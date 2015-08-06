package com.tadahtech.fadecloud.kd.map.structures;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.structures.strucs.Guardian;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * Created by Timothy Andis
 */
public class WorldEditAssit {

    private static final EditSession editSession;

    static {
        WorldEditPlugin plugin = JavaPlugin.getPlugin(WorldEditPlugin.class);
        com.sk89q.worldedit.world.World world = new BukkitWorld(Game.WORLD);
        editSession = plugin.getWorldEdit().getEditSessionFactory().getEditSession(world, Integer.MAX_VALUE);
    }

    public static boolean structureCheck(Island island, GridLocation loc, Structure structure, int level) {
        // // fixme
        StructureSchematic structureSchematic = structure.getSchematic(level);
        Block block = loc.toWorldLocation(island).add(8 - structureSchematic.getCenterX(), 64, 8 - structureSchematic.getCenterZ()).getBlock();
        CuboidClipboard clipboard = structureSchematic.getClipboard();
        for (int x = 0; x < clipboard.getWidth(); x++) {
            for (int y = 0; y < clipboard.getHeight(); y++) {
                for (int z = 0; z < clipboard.getLength(); z++) {
                    Block relative = block.getRelative(x, y, z);
                    Vector vector = new Vector(x, y, z);
                    if (structureSchematic.isBlock(relative) && structureSchematic.isBlockAt(vector)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation") // CuboidClipboard doesn't have a replacement yet.
    public static boolean pasteStructure(Island island, GridLocation loc, Structure structure, int rotation, int level, boolean fast) {
        Vector mainVector = loc.toWorldVector(island).add(0, 64, 0);
        Optional<Structure> previous = island.getStructure(loc);
        StructureSchematic structureSchematic = structure.getSchematic(level);

        if (previous.isPresent()) {
            // we have to manually remove the previous schematic
            StructureSchematic schematic = previous.get().getSchematic(level - 1);
            CuboidClipboard clipboard1 = schematic.getClipboard();
            clipboard1.rotate2D(previous.get().getRotation() * 90);
            BaseBlock AIR = new BaseBlock(0);
            for (int x = 0; x < clipboard1.getWidth(); x++) {
                for (int y = 0; y < clipboard1.getHeight(); y++) {
                    for (int z = 0; z < clipboard1.getLength(); z++) {
                        Vector relative = new Vector(x, y, z);
                        if (schematic.isBlockAt(relative)) {
                            editSession.rawSetBlock(mainVector.add(relative).add(8 - (clipboard1.getWidth() / 2), 0, 8 - (clipboard1.getLength() / 2)), AIR);
                        }
                    }
                }
            }
        }
        editSession.commit();
        //if (!fast && !structureCheck(island, loc, structure, level)) {
        //    pasteStructure(island, loc, structure, rotation, level - 1, fast);
        //    return false;
        //}
        CuboidClipboard clipboard = structureSchematic.getClipboard();
        clipboard.rotate2D(rotation * 90);
        for (int x = 0; x < clipboard.getWidth(); x++) {
            for (int y = 0; y < clipboard.getHeight(); y++) {
                for (int z = 0; z < clipboard.getLength(); z++) {
                    Vector vector = new Vector(x, y, z);
                    if (clipboard.getBlock(vector).getType() == structure.getType()) {
                        if(structure instanceof Guardian) {

                        }
                        continue;
                    }
                    editSession.rawSetBlock(mainVector.add(vector).add(8 - (clipboard.getWidth() / 2), 0, 8 - (clipboard.getLength() / 2)), clipboard.getBlock(vector));
                }
            }
        }
        structure.setGridLocation(loc);
        island.addStructure(loc, structure);
        editSession.commit();
        return true;
    }


}
