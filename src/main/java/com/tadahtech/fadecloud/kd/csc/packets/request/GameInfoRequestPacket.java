package com.tadahtech.fadecloud.kd.csc.packets.request;

import com.tadahtech.fadecloud.kd.csc.packets.RequestPacket;
import com.tadahtech.fadecloud.kd.csc.packets.ResponsePacket;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class GameInfoRequestPacket extends RequestPacket {

    public GameInfoRequestPacket(String server) {
        super(server);
    }

    @Override
    public ResponsePacket getResponse(String message) {
        return new GameInfoResponsePacket();
    }

    @Override
    public void write() {
        this.send("request");
    }
}
