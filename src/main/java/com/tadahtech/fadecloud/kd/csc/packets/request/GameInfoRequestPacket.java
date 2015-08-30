package com.tadahtech.fadecloud.kd.csc.packets.request;

import com.tadahtech.fadecloud.kd.csc.packets.RequestPacket;
import com.tadahtech.fadecloud.kd.csc.packets.ResponsePacket;
import com.tadahtech.fadecloud.kd.csc.packets.response.GameInfoResponsePacket;
import com.tadahtech.fadecloud.kd.sign.LobbySign;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class GameInfoRequestPacket extends RequestPacket {

    public GameInfoRequestPacket() {

    }

    private String signName;

    public GameInfoRequestPacket(String server, String signName) {
        super(server);
        this.signName = signName;
    }

    @Override
    protected void handleNoResponse() {
        LobbySign.get(signName).ifPresent(LobbySign::updateNoResponse);
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
