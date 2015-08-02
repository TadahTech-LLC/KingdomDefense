package com.tadahtech.fadecloud.kd.csc.packets;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.Packet;

/**
 * Created by Timothy Andis
 */
public class ServerInitPacket extends Packet {

    @Override
    public void write() {
        this.send("init-" + KingdomDefense.getInstance().getServerName() + "=" + KingdomDefense.getInstance().getUIName());
    }

    @Override
    public String getServer() {
        return KingdomDefense.getInstance().getHubServerName();
    }

    @Override
    public void handle(String message) {
        KingdomDefense plugin = KingdomDefense.getInstance();
        message = message.replace("init-", "");
        String server = message.split("=")[0];
        String ui = message.split("=")[1];
        plugin.getServerNames().putIfAbsent(ui, server);
    }
}
