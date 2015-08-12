package com.tadahtech.fadecloud.kd.csc.packets;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.Packet;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by Timothy Andis
 */
public abstract class RequestPacket extends Packet {

    private String server;
    private boolean response;
    private BukkitTask task;

    public RequestPacket() {

    }

    public boolean hasResponded() {
        return response;
    }

    public void respond() {
        this.response = true;
        if(task != null) {
            task.cancel();
        }
    }

    public RequestPacket(String server) {
        this.server = server;
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if(hasResponded()) {
                    //cant happen but OK!
                    cancel();
                    return;
                }
                handleNoResponse();
                cancel();
            }
        }.runTaskLater(KingdomDefense.getInstance(), 20L * 10);
    }

    protected abstract void handleNoResponse();

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public void handle(String message) {
        getResponse(message).write();
        new PacketResponse(getName()).write();
    }

    public abstract ResponsePacket getResponse(String message);

}
