package com.tadahtech.fadecloud.kd.king;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.king.attacks.CreeperKingAttack;
import com.tadahtech.fadecloud.kd.king.attacks.KnockbackAttack;
import com.tadahtech.fadecloud.kd.king.attacks.ZombieKingAttack;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.map.GameMap;
import com.tadahtech.fadecloud.kd.nms.CustomEntityType;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class King {

    private List<KingAttack> attacks;

    private String GREEN = ChatColor.GREEN + "❤";
    private String RED = ChatColor.RED + "❤";

    private EntityLiving entity;
    private double health;
    private CSTeam team;
    private PlayerInfo lastDamager;
    private Location spawn;

    public King(CSTeam team, GameMap map) {
        this.team = team;
        this.attacks = Lists.newArrayList();
        attacks.add(new KnockbackAttack());
        Optional<Location> maybe = map.getLocation(team.getLocationType());
        Location location = maybe.get();
        this.spawn = location;
        switch (team.getType()) {
            case ZOMBIE:
                this.entity = CustomEntityType.ZOMBIE.spawn(location.clone());
                this.attacks.add(new ZombieKingAttack());
                break;
            case CREEPER:
                this.entity = CustomEntityType.CREEPER.spawn(location.clone());
                this.attacks.add(new CreeperKingAttack());
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
        if (entity == null) {
            //cant happen;
            return;
        }
        this.health = 1000;
        LivingEntity entity = (LivingEntity) this.entity.getBukkitEntity();
        entity.setRemoveWhenFarAway(false);
        entity.setMetadata("king", new FixedMetadataValue(KingdomDefense.getInstance(), team.getType()));
        entity.setMaxHealth(health);
        entity.setHealth(health);
        entity.setCustomNameVisible(true);
        setHealth(health);
    }

    public String getPrettyHealth() {
        StringBuilder builder = new StringBuilder();
        int total = 10;
        int health = (int) (this.health / 50);
        for (int i = 0; i < health; i++) {
            builder.append(GREEN);
            total--;
        }
        for (int i = 0; i < total; i++) {
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
        LivingEntity livingEntity = (LivingEntity) entity.getBukkitEntity();
        livingEntity.setCustomName(getPrettyHealth() + ChatColor.RED + " (" + Math.round(health) + ")");
    }

    public CSTeam getTeam() {
        return team;
    }

    public void remove() {
        this.entity.getBukkitEntity().remove();
    }

    public void hit(double damage) {
        setHealth(getHealth() - damage);
        getTeam().getBukkitPlayers().stream().forEach(Lang.KING_BEING_ATTACKED::send);
        Random random = new Random();
        attacks.stream().forEach(KingAttack::hit);
        KingAttack kingAttack = attacks.get(random.nextInt(attacks.size()));
        if (!kingAttack.should()) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) this.entity.getBukkitEntity();
        livingEntity.getNearbyEntities(10, 10, 10)
          .stream()
          .filter(entity -> entity instanceof Player)
          .map(entity -> (Player) entity)
          .filter(player -> {
              PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
              return !info.getCurrentTeam().equals(this.getTeam());
          })
          .forEach(player -> {
              kingAttack.attack(player, this);
              kingAttack.message(player, this);
          });
    }

    public void setLastDamager(PlayerInfo lastDamager) {
        this.lastDamager = lastDamager;
    }

    public PlayerInfo getLastDamager() {
        return lastDamager;
    }

    public void respawn(boolean b) {
        this.entity.getBukkitEntity().teleport(spawn);
        if(b) {
            this.entity.setHealth(entity.getMaxHealth());
        }
    }
}
