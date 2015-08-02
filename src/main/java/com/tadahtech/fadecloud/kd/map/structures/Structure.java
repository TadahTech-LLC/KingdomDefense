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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public abstract class Structure extends Tickable {

    public static final String[] GUARDIAN_DESC = {
      " ",
      ChatColor.GREEN + "A healthy ally, is a good ally",
      ChatColor.GRAY + "Heals all players around it's radius"
    };

    public static final String[] BLAZE_DESC = {
      " ",
      ChatColor.RED + "Watch your enemies burn!",
      ChatColor.GRAY + "Shoots players with fireballs within it's range"
    };

    public static final String[] ARCHER_DESC = {
      " ",
      ChatColor.DARK_GRAY + "A basic defense is often the best",
      ChatColor.GRAY + "Shoots arrows at nearby enemies"
    };

    public static final String[] TESLA_DESC = {
      " ",
      ChatColor.DARK_AQUA + "Thor would like this one",
      ChatColor.GRAY + "Shoots lightning at nearby enemies"
    };
    private String name;
    protected List<Structure> structures = Lists.newArrayList();
    protected MultipleObjectThread<Structure> thread;
    protected Location firingLocation;
    protected double range;
    protected PlayerInfo owner;
    protected int level;

    static {
        File schematicFolder = new File(KingdomDefense.getInstance().getDataFolder(), "schematics/");
        if (!schematicFolder.exists() && !schematicFolder.mkdirs()) {
            KingdomDefense.getInstance().getLogger().warning("Couldn't create schematics folder");
        }
    }

    private Map<Integer, StructureSchematic> schematics = new HashMap<>();
    private String schematicName;

    public Structure(String name) {
        this.name = name;
        this.schematicName = ChatColor.stripColor(name.replace(" ", "_"));
        structures.add(this);
        if(thread == null) {
            this.thread = new MultipleObjectThread<>(structures, true);
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

    public Location getFiringLocation() {
        return firingLocation;
    }

    public void setFiringLocation(Location firingLocation) {
        this.firingLocation = firingLocation;
    }

    public StructureSchematic getSchematic(int level) {
        StructureSchematic structureSchematic = schematics.get(level);
        if (structureSchematic != null) {
            return structureSchematic;
        }

        try {
            KingdomDefense nations = KingdomDefense.getInstance();
            InputStream schematic = nations.getResource("structures/" + this.schematicName + level + ".schematic");
            if (schematic == null) {
                nations.getLogger().warning("Failed to find structure 'structures" + File.separator + this.schematicName + level + ".schematic'");
                return null;
            }
            CuboidClipboard clipboard = ((MCEditSchematicFormat) MCEditSchematicFormat.MCEDIT).load(schematic);
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
        return 500 * (level + 1);
    }

}
