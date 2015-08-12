package com.tadahtech.fadecloud.kd.map.structures.strucs;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.structures.GridLocation;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public abstract class DefenseStructure extends Structure {

    protected int fireRate = 1;
    protected double damage = 1;
    protected long last;

    public static ItemStack GUARDIAN, ARCHER, BLAZE, TESLA;

    static {
        GUARDIAN = new ItemBuilder(new Wool(DyeColor.LIME).toItemStack())
          .data(DyeColor.LIME.getWoolData())
          .amount(1)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Guardian Tower")
          .build();
        ARCHER = new ItemBuilder(new Wool(DyeColor.GRAY).toItemStack())
          .data(DyeColor.GRAY.getWoolData())
          .amount(1)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Archer Tower")
          .build();
        BLAZE = new ItemBuilder(new Wool(DyeColor.RED).toItemStack())
          .data(DyeColor.RED.getWoolData())
          .amount(1)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Blaze Tower")
          .build();
        TESLA = new ItemBuilder(new Wool(DyeColor.CYAN).toItemStack())
          .data(DyeColor.CYAN.getWoolData())
          .amount(1)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Tesla Tower")
          .build();
    }

    protected Map<Integer, List<Vector>> launchSourceVectors;

    public DefenseStructure(String name) {
        super(name);
        this.launchSourceVectors = Maps.newHashMap();
    }

    protected void targetPlayer(Player player) {

    }

    @SuppressWarnings("deprecation") // CuboidClipboard doesn't have a replacement yet
    private List<Vector> getLaunchSources(int level) {
        List<Vector> sources = launchSourceVectors.get(level);
        if (sources != null) {
            return sources;
        }

        sources = new ArrayList<>();

        CuboidClipboard clipboard = getSchematic(level).getClipboard();
        for (int x = 0; x < clipboard.getWidth(); x++) {
            for (int y = 0; y < clipboard.getHeight(); y++) {
                for (int z = 0; z < clipboard.getLength(); z++) {
                    Vector vec = new Vector(x, y, z);
                    BaseBlock block = clipboard.getBlock(vec);

                    if (block == null || block.getType() != getType()) {
                        continue;
                    }

                    sources.add(vec.add(8 - getSchematic(getLevel()).getCenterX(), 0, 8 - getSchematic(getLevel()).getCenterZ()));
                }
            }
        }
        launchSourceVectors.put(level, sources);
        return sources;
    }

    @Override
    public void tick() {
        long curr = System.currentTimeMillis();
        if(fireRate - (curr - last) > 0) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
            if(info.getCurrentTeam().equals(this.owner.getCurrentTeam())) {
                continue;
            }
            // Identify closest launch source to the player and shoot
            Optional<Location> maybeLaunchSource = getLaunchSources(getLevel()).stream()
              .map(vec -> getLocation().toWorldVector(info.getCurrentTeam().getIsland()).add(vec))
              .map(vec -> new Location(Game.WORLD, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()))
              .collect(Collectors.minBy(Comparator.comparingDouble(loc -> loc.distanceSquared(player.getLocation()))));
            if(maybeLaunchSource.isPresent()) {
                return;
            }
            Location launchSource = player.getWorld().getHighestBlockAt(maybeLaunchSource.get()).getRelative(BlockFace.UP).getLocation();
            Location directionLoc = player.getLocation().subtract(launchSource);
            Vector direction = new Vector(directionLoc.getX(), directionLoc.getY(), directionLoc.getZ()).normalize();
            launchProjectile(launchSource, direction);
            targetPlayer(player);
        }
    }

    public abstract void launchProjectile(Location source, Vector direction);

    public int getFireRate() {
        return fireRate;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public GridLocation getLocation() {
        return location;
    }
}
