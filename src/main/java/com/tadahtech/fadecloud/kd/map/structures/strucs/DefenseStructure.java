package com.tadahtech.fadecloud.kd.map.structures.strucs;

import com.sk89q.worldedit.Vector;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import java.util.Optional;

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

    public DefenseStructure(String name) {
        super(name);
    }

    protected void targetPlayer(Player player) {

    }

    @Override
    public void tick() {
        long curr = System.currentTimeMillis();
        if(fireRate - ((curr - last) / 1000) > 0) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
            if(info.getCurrentTeam().equals(this.owner.getCurrentTeam())) {
                continue;
            }
            Optional<Location> maybeLaunchSource = Optional.ofNullable(getFiring());
            if(!maybeLaunchSource.isPresent()) {
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

}
