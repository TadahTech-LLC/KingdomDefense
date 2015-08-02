package com.tadahtech.fadecloud.kd.nms;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.map.GameMap;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Optional;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class King {

    private EntityLiving entity;
    private double health;
    private CSTeam team;

    public King(CSTeam team, GameMap map) {
        this.team = team;
        Optional<Location> location = map.getLocation(team.getLocationType());
        switch (team.getType()) {
            case ZOMBIE:
                this.entity = CustomEntityType.spawnZombie(location.get(), false);
                break;
            case CREEPER:
                this.entity = CustomEntityType.CREEPER.spawn(location.get());
                break;
            case ENDERMAN:
                this.entity = CustomEntityType.ENDERMAN.spawn(location.get());
                break;
            case SKELETON:
                this.entity = CustomEntityType.SKELETON.spawn(location.get());
                break;
            default:
                return;
        }
        if(entity == null) {
            //cant happen;
            return;
        }
        this.health = 1000;
        this.entity.getBukkitEntity().setMetadata("king", new FixedMetadataValue(KingdomDefense.getInstance(), team.getType()));
        this.entity.getAttributeInstance(GenericAttributes.maxHealth).setValue(health);
    }

    public Entity getEntity() {
        return entity;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public CSTeam getTeam() {
        return team;
    }
}
