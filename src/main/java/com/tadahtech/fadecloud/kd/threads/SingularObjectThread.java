package com.tadahtech.fadecloud.kd.threads;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by Timothy Andis
 */
public class SingularObjectThread<T extends Tickable> {

    private T t;
    private BukkitTask task;

    public SingularObjectThread(T object, boolean waitForGame) {
        this(object, waitForGame, 1);
    }

    public SingularObjectThread(T object, boolean waitForGame, int i) {
        this.t = object;
        int delay = 0;
        if (waitForGame) {
            delay = 20 * 60 * 30;
        }
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                t.tick();
            }
        }.runTaskTimer(KingdomDefense.getInstance(), delay, i);
    }

    public T get() {
        return t;
    }

    public void cancel() {
        this.task.cancel();
    }
}
