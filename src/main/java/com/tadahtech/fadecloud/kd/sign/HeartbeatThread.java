package com.tadahtech.fadecloud.kd.sign;


import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.RequestPacket;
import com.tadahtech.fadecloud.kd.csc.packets.request.GameInfoRequestPacket;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by Timothy Andis
 */
public class HeartbeatThread extends BukkitRunnable {

    public HeartbeatThread() {
        this.runTaskTimerAsynchronously(KingdomDefense.getInstance(), 20L, 10L);
    }

    private static List<GameInfoRequestPacket> packetList = Lists.newArrayList();

    @Override
    public void run() {
        LobbySign.getAll().forEach(lobbySign -> {
            String to = KingdomDefense.getInstance().getServerNames().get(lobbySign.getArena());
            GameInfoRequestPacket packet = new GameInfoRequestPacket(to, lobbySign.getArena());
            packet.write();
            packetList.add(packet);
        });
    }

    public static void respond() {
        List<GameInfoRequestPacket> packets = Lists.newArrayList(packetList);
        packets.stream().forEach(RequestPacket::respond);
        packetList.clear();
    }
}
