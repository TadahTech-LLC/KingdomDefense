package com.tadahtech.fadecloud.kd.nms.mobs;

import com.tadahtech.fadecloud.kd.nms.NMS;
import net.minecraft.server.v1_7_R4.*;


/**
 * Created by Timothy Andis
 */
public class KDVillager extends EntityVillager {

    public KDVillager(World world) {
        super(world);
        NMS.clearGoals(targetSelector, goalSelector);
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        return null;
    }

    @Override
    public void collide(Entity other) {

    }

    @Override
    public void g(double d, double dd, double ddd) {

    }
}
