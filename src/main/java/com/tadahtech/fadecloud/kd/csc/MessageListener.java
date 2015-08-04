package com.tadahtech.fadecloud.kd.csc;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import redis.clients.jedis.JedisPubSub;

/**
 * @author Tim [calebbfmv]
 *         Created by Tim [calebbfmv] on 11/12/2014.
 */
public class MessageListener extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if (!channel.equalsIgnoreCase(JedisManager.CHANNEL)) {
            return;
        }

        String[] str = message.split("%");
        String packetRaw = str[0];
        String msg = str[1];
        String server = str[2];
        Packet packet = Packet.getPacket(packetRaw);

        if (packet == null) {
            return;
        }
        
        if(!server.equalsIgnoreCase(KingdomDefense.getInstance().getServerName())) {
            return;
        }

        packet.handle(msg);
    }

    @Override
    public void onPMessage(String s, String s2, String s3) {

    }

    @Override
    public void onSubscribe(String s, int i) {

    }

    @Override
    public void onUnsubscribe(String s, int i) {

    }

    @Override
    public void onPUnsubscribe(String s, int i) {

    }

    @Override
    public void onPSubscribe(String s, int i) {

    }
}
