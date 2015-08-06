package com.tadahtech.fadecloud.kd.sign;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.request.GameInfoRequestPacket;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Timothy Andis
 */
public class HeartbeatThread extends BukkitRunnable {

    public HeartbeatThread() {
        this.runTaskTimerAsynchronously(KingdomDefense.getInstance(), 20L, 10L);
    }

    @Override
    public void run() {
        LobbySign.getAll().forEach(lobbySign -> {
            String to = KingdomDefense.getInstance().getServerNames().get(lobbySign.getArena());
            new GameInfoRequestPacket(to).write();
        });
    }
}
