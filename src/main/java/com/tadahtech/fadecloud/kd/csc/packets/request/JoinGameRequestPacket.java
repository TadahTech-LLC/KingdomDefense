package com.tadahtech.fadecloud.kd.csc.packets.request;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.RequestPacket;
import com.tadahtech.fadecloud.kd.csc.packets.ResponsePacket;
import com.tadahtech.fadecloud.kd.csc.packets.response.JoinGameResponsePacket;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class JoinGameRequestPacket extends RequestPacket {

    private UUID uuid;

    public JoinGameRequestPacket(String server, Player player) {
        super(server);
        this.uuid = player.getUniqueId();
    }

    @Override
    public ResponsePacket getResponse(String message) {
        return new JoinGameResponsePacket(uuid.toString(), KingdomDefense.getInstance().getServerName());
    }

    @Override
    public void write() {
        this.send(uuid.toString());
    }
}
