package com.tadahtech.fadecloud.kd.csc.packets.request;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.RequestPacket;
import com.tadahtech.fadecloud.kd.csc.packets.ResponsePacket;
import com.tadahtech.fadecloud.kd.csc.packets.response.JoinGameResponsePacket;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class JoinGameRequestPacket extends RequestPacket {

    public JoinGameRequestPacket() {

    }

    @Override
    protected void handleNoResponse() {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) {
            //welp, idiot.
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Lang.NO_GAME_FOUND.send(info);
    }

    private UUID uuid;

    public JoinGameRequestPacket(String server, Player player) {
        super(server);
        this.uuid = player.getUniqueId();
    }

    @Override
    public ResponsePacket getResponse(String message) {
        respond();
        return new JoinGameResponsePacket(UUID.fromString(message).toString(), KingdomDefense.getInstance().getServerName());
    }

    @Override
    public void write() {
        this.send(uuid.toString());
    }
}
