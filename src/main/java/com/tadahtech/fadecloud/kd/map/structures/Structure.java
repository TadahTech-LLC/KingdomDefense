package com.tadahtech.fadecloud.kd.map.structures;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.StructureType;
import com.tadahtech.fadecloud.kd.map.structures.strucs.Guardian;
import com.tadahtech.fadecloud.kd.threads.MultipleObjectThread;
import com.tadahtech.fadecloud.kd.threads.Tickable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public abstract class Structure extends Tickable {

    public static final String[] GUARDIAN_DESC = {
      ChatColor.GREEN + "A healthy ally, is a good ally",
      ChatColor.GRAY + "Heals all players around it's radius",
      ChatColor.GRAY.toString() + "Cost: "+ ChatColor.AQUA + StructureType.GUARDIAN.getCost()

    };

    public static final String[] BLAZE_DESC = {
      ChatColor.RED + "Watch your enemies burn!",
      ChatColor.GRAY + "Shoots players with fireballs within it's range",
      ChatColor.GRAY.toString() + "Cost: "+ ChatColor.AQUA + StructureType.BLAZE.getCost()
    };

    public static final String[] ARCHER_DESC = {
      ChatColor.DARK_GRAY + "A basic defense is often the best",
      ChatColor.GRAY + "Shoots arrows at nearby enemies",
      ChatColor.GRAY.toString() + "Cost: "+ ChatColor.AQUA + StructureType.ARCHER.getCost()

    };

    public static final String[] TESLA_DESC = {
      ChatColor.DARK_AQUA + "Thor would like this one",
      ChatColor.GRAY + "Shoots lightning at nearby enemies",
      ChatColor.GRAY.toString() + "Cost: "+ ChatColor.AQUA + StructureType.TESLA.getCost()

    };

    private String name;
    protected List<Structure> structures = Lists.newArrayList();
    protected MultipleObjectThread<Structure> thread;
    protected GridLocation location;
    protected double range;
    protected PlayerInfo owner;
    protected int level;
    private static File schematicFolder;

    static {
        schematicFolder = new File(KingdomDefense.getInstance().getDataFolder(), "structures");
        if (!schematicFolder.exists() && !schematicFolder.mkdirs()) {
            KingdomDefense.getInstance().getLogger().warning("Couldn't create schematics folder");
        }
    }

    private Map<Integer, StructureSchematic> schematics = new HashMap<>();
    private String schematicName;
    private int rotation;

    public Structure(String name) {
        this.name = name;
        this.schematicName = ChatColor.stripColor(name.replace(" ", "_"));
        structures.add(this);
        if(thread == null) {
            this.thread = new MultipleObjectThread<>(structures, true);
        } else {
            this.thread.get().add(this);
        }
    }

    public PlayerInfo getOwner() {
        return owner;
    }

    public void setOwner(PlayerInfo owner) {
        this.owner = owner;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    @SuppressWarnings("ConstantConditions")
    public StructureSchematic getSchematic(int level) {
        StructureSchematic structureSchematic = schematics.get(level);
        if (structureSchematic != null) {
            return structureSchematic;
        }
        try {
            if(schematicFolder.listFiles() == null) {
                return null;
            }
            KingdomDefense nations = KingdomDefense.getInstance();
            File file = null;
            for(File possible : schematicFolder.listFiles()) {
                if(possible.getName().equalsIgnoreCase(this.schematicName + "-" + level + ".schematic")) {
                    file = possible;
                    break;
                }
            }
            if (file == null) {
                nations.getLogger().warning("Failed to find structure " + this.schematicName + "-" + level + ".schematic'");
                return null;
            }

            CuboidClipboard clipboard = MCEditSchematicFormat.MCEDIT.load(file);
            structureSchematic = new StructureSchematic(clipboard, level);

            schematics.put(level, structureSchematic);
            return structureSchematic;
        } catch (IOException | DataException e) {
            KingdomDefense.getInstance().getLogger().severe("Couldn't load structure schematic");
            e.printStackTrace();
        }
        return null;
    }

    public int getType() {
        if(this instanceof Guardian) {
            return Material.BEACON.getId();
        }
        return Material.GOLD_BLOCK.getId();
    }

    public String getName() {
        return name;
    }

    public abstract void give(Player player);

    public abstract StructureType getStructureType();

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public int getNextCost() {
        return getStructureType().getCost((level + 1));
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setGridLocation(GridLocation gridLocation) {
        this.location = gridLocation;
    }

    public GridLocation getLocation() {
        return location;
    }
}
