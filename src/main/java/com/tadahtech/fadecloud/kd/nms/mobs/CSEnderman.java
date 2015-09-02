package com.tadahtech.fadecloud.kd.nms.mobs;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.nms.NMS;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class CSEnderman extends EntityEnderman {

    public CSEnderman(World world) {
        super(world);
        NMS.clearGoals(targetSelector, goalSelector);
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        getBukkitEntity().setMetadata("noBurn", new FixedMetadataValue(KingdomDefense.getInstance(), true));
    }

    @Override
    public void collide(Entity entity) {
        
    }

    @Override
    public void g(double d, double d2, double d1){

    }

    @Override
    public boolean bZ() {
        return false;
    }

    @Override
    public boolean k(double d, double dd, double ddd) {
        return false;
    }
}
