package com.tadahtech.fadecloud.kd.nms;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.minecraft.server.v1_8_R2.EntityCreature;

/**
 * Created by Timothy Andis
 */
public interface CustomEntity {

    EntityCreature get();

    PlayerInfo getOwner();

}
