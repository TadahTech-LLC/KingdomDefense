package com.tadahtech.fadecloud.kd.csc;

import com.tadahtech.fadecloud.kd.KingdomDefense;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tim [calebbfmv]
 *         Created by Tim [calebbfmv] on 11/12/2014.
 */
public abstract class Packet {

    private String name;
    private int lastId = 0;

    protected static final String HUB = KingdomDefense.getInstance().getHubServerName();

    private static Map<String, Packet> packets = new HashMap<>();

    public Packet() {
        this.name = getClass().getSimpleName();
        this.lastId = lastId + 1;
        packets.putIfAbsent(name, this);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return lastId;
    }

    public abstract void write();

    public abstract String getServer();

    public abstract void handle(String message);

    public static Packet getPacket(String name) {
        return packets.get(name);
    }

    protected void send(String message) {
        KingdomDefense.getInstance().getJedisManager().sendInfo(this, message, getServer());
    }
}
