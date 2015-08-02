package com.tadahtech.fadecloud.kd.map.structures.strucs;

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
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public abstract class DefenseStructure extends Structure {

    protected int fireRate;
    protected double damage;
    protected long last;
    protected boolean has;
    protected Player target;

    public static ItemStack GUARDIAN, ARCHER, BLAZE, TESLA;

    static {
        GUARDIAN = new ItemBuilder(new Wool(DyeColor.LIME).toItemStack())
          .amount(1)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Guardian Tower")
          .build();
        ARCHER = new ItemBuilder(new Wool(DyeColor.GRAY).toItemStack())
          .amount(1)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Archer Tower")
          .build();
        BLAZE = new ItemBuilder(new Wool(DyeColor.RED).toItemStack())
          .amount(1)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Blaze Tower")
          .build();
        TESLA = new ItemBuilder(new Wool(DyeColor.CYAN).toItemStack())
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
        if(getFiringLocation() == null) {
            //hasnt been placed
            return;
        }
        if(has) {
            if(target == null || target.isDead()) {
                has = false;
                target = null;
            } else {
                Location location = target.getLocation();
                double z = location.getZ();
                double x = location.getX();
                double locZ = firingLocation.getZ();
                double locX = firingLocation.getX();
                double distance = Math.sqrt(NumberConversions.square(locX - x) + NumberConversions.square(locZ - z));
                if (distance < getRange()) {
                    return;
                }
                target = null;
                has = false;
            }
        }
        long curr = System.currentTimeMillis();
        if(fireRate - (curr - last) > 0) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
            if(info.getCurrentTeam().equals(this.owner.getCurrentTeam())) {
                continue;
            }
            Location location = player.getLocation();
            double z = location.getZ();
            double x = location.getX();
            double locZ = firingLocation.getZ();
            double locX = firingLocation.getX();
            double distance = Math.sqrt(NumberConversions.square(locX - x) + NumberConversions.square(locZ - z));
            if (distance > getRange()) {
                continue;
            }
            Location launchSource = player.getWorld().getHighestBlockAt(getFiringLocation()).getRelative(BlockFace.UP).getLocation();
            Location directionLoc = player.getLocation().subtract(launchSource);
            Vector direction = new Vector(directionLoc.getX(), directionLoc.getY(), directionLoc.getZ()).normalize();
            launchProjectile(launchSource, direction);
            targetPlayer(player);
            has = true;
            target = player;
            break;
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
