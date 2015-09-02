package com.tadahtech.fadecloud.kd.map.structures;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.map.StructureType;
import com.tadahtech.fadecloud.kd.map.structures.strucs.Guardian;
import com.tadahtech.fadecloud.kd.threads.Tickable;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

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
    private static List<Structure> structures = Lists.newArrayList();
    protected double range;
    protected PlayerInfo owner;
    protected int level;
    protected Location firing;

    private String baseName;
    public StructureTickThread THREAD;
    private boolean active;
    private Hologram hologram;

    public Structure(String name) {
        this.name = name;
        this.firing = null;
        this.baseName = ChatColor.stripColor(name.replace(" ", "_"));
        this.range = 20;
        structures.add(this);
        if(THREAD == null) {
            THREAD = new StructureTickThread();
            THREAD.runTaskTimer(KingdomDefense.getInstance(), 20L, 20L);
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
        this.hologram.clearLines();
        hologram.appendTextLine(ChatColor.DARK_AQUA + baseName + " " + ChatColor.GOLD + getLevel());
    }

    public int getLevel() {
        return level;
    }

    public int getNextCost() {
        return getStructureType().getCost((level + 1));
    }

    public Location getFiring() {
        return firing;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setFiring(Location firing) {
        this.firing = firing;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void activate(PlayerInfo info) {
        setLevel(1);
        setActive(true);
        setOwner(info);
        this.hologram = HologramsAPI.createHologram(KingdomDefense.getInstance(), firing.clone().add(0, 0.3, 0));
        hologram.appendTextLine(ChatColor.DARK_AQUA + baseName + " " + ChatColor.GOLD + getLevel());
        if (this instanceof Guardian) {
            Guardian guardian = (Guardian) this;
            guardian.setHealthPerTick(2);
            guardian.setCooldown(20);
        }
        info.getBukkitPlayer().playSound(info.getBukkitPlayer().getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        info.sendMessage(Lang.PREFIX + getBaseName() + ChatColor.YELLOW + " activated!");
    }

    public class StructureTickThread extends BukkitRunnable {

        @Override
        public void run() {
            structures.forEach(structure -> {
                if(!structure.isActive()) {
                    return;
                }
                if(owner == null) {
                    return;
                }
                if(KingdomDefense.getInstance().getGame().getState() != GameState.BATTLE) {
                    return;
                }
                structure.tick();
            });
        }
    }
}
