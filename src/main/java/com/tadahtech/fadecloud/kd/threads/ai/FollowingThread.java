package com.tadahtech.fadecloud.kd.threads.ai;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.nms.mobs.CSZombie;
import com.tadahtech.fadecloud.kd.threads.AIThread;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.PathEntity;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class FollowingThread extends AIThread {

    @Override
    public void run() {
        ENTITES.stream().forEach(customEntity ->{
            CSZombie entity = (CSZombie) customEntity.get();
            PlayerInfo owner = customEntity.getOwner();
            Player player = owner.getBukkitPlayer();
            if(entity == null) {
                return;
            }
            if(!entity.isAlive()) {
                return;
            }
            if(entity.getGoalTarget() != null && entity.getGoalTarget().isAlive()) {
                return;
            }
            if(--entity.ticks <= 0) {
                entity.ticks = 10;
                double tpdistance = 40;
                double stop = 6;
                if (player.isFlying()) {
                    return;
                }
                EntityPlayer human = ((CraftPlayer) player).getHandle();
                if(!human.onGround) {
                    return;
                }
                if (entity.h(human) > tpdistance) {
                    entity.getBukkitEntity().teleport(player.getLocation().clone().add(1.5, 0, 1.5));
                    return;
                }
                if (entity.h(human) <= stop) {
                    entity.getNavigation().a((PathEntity) null, 0D);
                    return;
                }
                PathEntity path = entity.getNavigation().a(human);
                entity.getNavigation().a(path, 1.5D);
            }
        });
    }
}
