package com.tadahtech.fadecloud.kd.csc.packets;

import com.tadahtech.fadecloud.kd.csc.Packet;

/**
 * Created by Timothy Andis
 */
public abstract class ResponsePacket extends Packet {

    @Override
    public String getServer() {
        return HUB;
    }
}
