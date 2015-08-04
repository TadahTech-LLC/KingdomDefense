package com.tadahtech.fadecloud.kd.csc;

import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Tim [calebbfmv]
 *         Created by Tim [calebbfmv] on 11/12/2014.
 */
public class JedisManager {

    public static String CHANNEL = "NationsCSC";
    private String host, password;
    private JedisPool pool;

    public JedisManager(FileConfiguration config) {
        try {
            String host = config.getString("host");
            String password = config.getString("password");
            this.host = host;
            this.password = password;
            this.pool = new JedisPool(host);
            subscribe();
        } catch (Exception e) {
            System.out.println("ERROR Connecting to Redis! Is it running? Exception: " + e.getMessage());
        }
    }

    private void subscribe() {
        new Thread(() -> {
            Jedis subscriber = new Jedis(host);
            subscriber.subscribe(new MessageListener(), CHANNEL);
        }).start();
    }

    public void sendInfo(Packet packet, String message, String server) {
        Jedis jedis = pool.getResource();
        try {
            jedis.publish(CHANNEL, packet.getName() + "%" + message + "%" + server);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.returnResource(jedis);
        }
    }

}
