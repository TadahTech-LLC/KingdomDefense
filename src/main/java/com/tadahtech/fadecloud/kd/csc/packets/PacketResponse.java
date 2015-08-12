package com.tadahtech.fadecloud.kd.csc.packets;

import com.tadahtech.fadecloud.kd.csc.Packet;

/**
 * Created by Timothy Andis
 */
public class PacketResponse extends Packet {

    private String string;

    public PacketResponse(String name) {
        this.string = name;
    }

    @Override
    public void write() {
        this.send(string);
    }

    @Override
    public String getServer() {
        return HUB;
    }

    @Override
    public void handle(String message) {
        RequestPacket packet = (RequestPacket) getPacket(message);
        packet.respond();
    }
}
