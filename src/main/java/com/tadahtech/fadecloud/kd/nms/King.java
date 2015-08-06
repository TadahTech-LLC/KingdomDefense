package com.tadahtech.fadecloud.kd.nms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.map.GameMap;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.EntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Optional;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class King {

    private String GREEN = ChatColor.GREEN + "❤";
    private String RED = ChatColor.RED + "❤";

    private EntityLiving entity;
    private double health;
    private CSTeam team;
    private Hologram hologram;

    public King(CSTeam team, GameMap map) {
        this.team = team;
        Optional<Location> maybe = map.getLocation(team.getLocationType());
        Location location = maybe.get();
        location = location.getWorld().getHighestBlockAt(location).getLocation();
        switch (team.getType()) {
            case ZOMBIE:
                this.entity = CustomEntityType.ZOMBIE.spawn(location.clone());
                break;
            case CREEPER:
                this.entity = CustomEntityType.CREEPER.spawn(location.clone());
                break;
            case ENDERMAN:
                this.entity = CustomEntityType.ENDERMAN.spawn(location.clone());
                break;
            case SKELETON:
                this.entity = CustomEntityType.SKELETON.spawn(location.clone());
                break;
            default:
                return;
        }
        if(entity == null) {
            //cant happen;
            return;
        }
        this.health = 500;
        LivingEntity entity = (LivingEntity) this.entity.getBukkitEntity();
        entity.setRemoveWhenFarAway(false);
        entity.setMetadata("king", new FixedMetadataValue(KingdomDefense.getInstance(), team.getType()));
        entity.setMaxHealth(health);
        entity.setHealth(health);
        this.hologram = HologramsAPI.createHologram(KingdomDefense.getInstance(), entity.getLocation().add(0, entity.getEyeHeight() + 0.2, 0));
        this.hologram.appendTextLine(getPrettyHealth());
    }

    public String getPrettyHealth() {
        StringBuilder builder = new StringBuilder();
        int total = 10;
        int health = (int) (this.health / 50);
        for(int i = 0; i < health; i++) {
            builder.append(GREEN);
            total--;
        }
        for(int i = 0; i < total; i++) {
            builder.append(RED);
        }
        return builder.toString();
    }

    public Entity getEntity() {
        return entity;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
        this.hologram.clearLines();
        this.hologram.appendTextLine(getPrettyHealth());
    }

    public CSTeam getTeam() {
        return team;
    }
}
