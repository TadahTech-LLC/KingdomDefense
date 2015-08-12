package com.tadahtech.fadecloud.kd.threads;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Created by Timothy Andis
 */
public class MultipleObjectThread<T extends Tickable> {

    private List<T> objects;
    private BukkitTask task;

    public MultipleObjectThread(List<T> object, boolean waitForGame) {
        this.objects = object;
        int delay = 0;
        if (waitForGame) {
            delay = 20 * 60 * 5;
        }
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                objects.forEach(T::tick);
            }
        }.runTaskTimer(KingdomDefense.getInstance(), delay, 1L);
    }

    public List<T> get() {
        return objects;
    }

    public void cancel() {
        this.task.cancel();
    }
}