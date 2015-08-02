package com.tadahtech.fadecloud.kd.csc.packets;

import com.tadahtech.fadecloud.kd.csc.Packet;

/**
 * Created by Timothy Andis
 */
public abstract class RequestPacket extends Packet {

    private String server;

    public RequestPacket(String server) {
        this.server = server;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public void handle(String message) {
        getResponse(message).write();
    }

    public abstract ResponsePacket getResponse(String message);

}
