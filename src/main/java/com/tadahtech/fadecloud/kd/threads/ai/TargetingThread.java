package com.tadahtech.fadecloud.kd.threads.ai;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.nms.CustomEntity;
import com.tadahtech.fadecloud.kd.threads.AIThread;
import net.minecraft.server.v1_8_R2.EntityCreature;
import net.minecraft.server.v1_8_R2.EntityLiving;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class TargetingThread extends AIThread {

    @Override
    public void run() {
        List<CustomEntity> entities = Lists.newArrayList(ENTITES);
        entities.stream().forEach(customEntity -> {
            EntityCreature entity = customEntity.get();
            if(!entity.isAlive()) {
                ENTITES.remove(customEntity);
                return;
            }
            if(entity.getGoalTarget() != null) {
                EntityLiving living = entity.getGoalTarget();
                if(!living.isAlive()) {
                    entity.setGoalTarget(null);
                    return;
                }
                return;
            }
            LivingEntity bukkitEntity = (LivingEntity) entity.getBukkitEntity();
            List<Player> players = bukkitEntity.getNearbyEntities(20, 20, 20)
              .stream()
              .filter(e -> e instanceof Player)
              .map(e -> (Player) e)
              .filter(player -> {
                  PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
                  return info != null && !(info.getCurrentTeam() != null && info.getCurrentTeam().equals(customEntity.getOwner().getCurrentTeam())) && !info.equals(customEntity.getOwner());
              })
              .collect(Collectors.toList());
            if(players.isEmpty()) {
                return;
            }
            Player player = players.get(new Random().nextInt(players.size()));
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.setGoalTarget(entityPlayer);
                }
            }.runTask(KingdomDefense.getInstance());
        });
    }
}
