package com.tadahtech.fadecloud.kd.map.structures.strucs;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.map.StructureType;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.zombie.ZombieTeam;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.concurrent.TimeUnit;

/**
 * Created by Timothy Andis
 */
public class Guardian extends Structure {

    private double healthPerTick;
    private long lastHeal;
    private int cooldown;

    public Guardian(PlayerInfo info) {
        super(ChatColor.GREEN.toString() + ChatColor.BOLD + "Guardian");
        this.setOwner(info);
        this.cooldown = 20;
        this.lastHeal = System.currentTimeMillis();
        info.setCurrentStructure(this);
    }

    @Override
    public void tick() {
        long diff = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastHeal);
        if(diff - cooldown > 0) {
            return;
        }
        this.lastHeal = System.currentTimeMillis();
        for (Player player : owner.getCurrentTeam().getBukkitPlayers()) {
            Location location = player.getLocation();
            double z = location.getZ();
            double x = location.getX();
            double locZ = firingLocation.getZ();
            double locX = firingLocation.getX();
            double distance = Math.sqrt(NumberConversions.square(locX - x) + NumberConversions.square(locZ - z));
            if (distance > getRange()) {
                continue;
            }
            PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
            if (!info.getCurrentTeam().equals(owner.getCurrentTeam())) {
                continue;
            }
            CSTeam team = info.getCurrentTeam();
            double health = healthPerTick;
            if (team instanceof ZombieTeam) {
                health *= 0.3;
            }
            player.setHealth(player.getHealth() + health);
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.8F);
            int yaw = (int) player.getLocation().getYaw() + 45;
            while (yaw < 0)
                yaw += 360;
            while (yaw > 360)
                yaw -= 360;
            int direction = yaw / 90;
            Block b = player.getLocation().getBlock();
            switch (direction) {
                case 0:
                    b = b.getRelative(BlockFace.SOUTH);
                    break;
                case 1:
                    b = b.getRelative(BlockFace.WEST);
                    break;
                case 2:
                    b = b.getRelative(BlockFace.NORTH);
                    break;
                case 3:
                    b = b.getRelative(BlockFace.EAST);
                    break;
            }
            Location display = b.getLocation().clone().add(0, player.getEyeHeight(), 0);
            for(int i = 20; i > 0; i--) {
                player.playEffect(display.clone(), Effect.HAPPY_VILLAGER, 0);
            }
        }
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public double getHealthPerTick() {
        return healthPerTick;
    }

    public void setHealthPerTick(double healthPerTick) {
        this.healthPerTick = healthPerTick;
    }

    @Override
    public void give(Player player) {
        player.getInventory().addItem(DefenseStructure.GUARDIAN);
    }

    @Override
    public StructureType getStructureType() {
        return StructureType.GURADIAN;
    }
}
