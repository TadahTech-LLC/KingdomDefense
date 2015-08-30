package com.tadahtech.fadecloud.kd.threads;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.nms.CustomEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timothy Andis
 */
public abstract class AIThread extends BukkitRunnable {

    public static List<CustomEntity> ENTITES = new ArrayList<>();

    public AIThread() {
        this.runTaskTimerAsynchronously(KingdomDefense.getInstance(), 20 * 60 * 5, 15L);
    }

}
