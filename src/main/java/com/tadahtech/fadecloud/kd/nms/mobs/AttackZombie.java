package com.tadahtech.fadecloud.kd.nms.mobs;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.nms.CustomEntity;
import com.tadahtech.fadecloud.kd.nms.CustomEntityType;
import com.tadahtech.fadecloud.kd.threads.AIThread;
import net.minecraft.server.v1_8_R2.EntityCreature;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class AttackZombie implements CustomEntity {

    private CSZombie zombie;
    private PlayerInfo info;

    public AttackZombie(PlayerInfo info) {
        this.info = info;
        Player player = info.getBukkitPlayer();
        Location location = player.getLocation();
        this.zombie = CustomEntityType.spawnZombie(location, true);
        AIThread.ENTITES.add(this);
    }

    @Override
    public EntityCreature get() {
        return zombie;
    }

    @Override
    public PlayerInfo getOwner() {
        return info;
    }
}
