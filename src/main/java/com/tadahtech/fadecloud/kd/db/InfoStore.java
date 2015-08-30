package com.tadahtech.fadecloud.kd.db;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.UUID;

/**
 * @author Timothy Andis
 */
public class InfoStore {

    private JedisPool jedisPool;
    private final String NAMESPACE = "player_info";

    public InfoStore(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void load(UUID uuid) {
        Jedis jedis = jedisPool.getResource();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String message = jedis.get(NAMESPACE + ":" + uuid.toString());
                    if (message == null) {
                        new PlayerInfo(uuid);
                        return;
                    }
                    PlayerInfo.fromString(message);
                } catch (Exception ignored) {

                } finally {
                    jedisPool.returnResource(jedis);
                }
            }
        }.runTaskAsynchronously(KingdomDefense.getInstance());

    }

    public void save(PlayerInfo info) {
        Jedis jedis = jedisPool.getResource();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    jedis.set(NAMESPACE + ":" + info.getUuid().toString(), info.toString());
                } catch (JedisException ignored) {
                    ignored.printStackTrace();
                } finally {
                    jedisPool.returnResource(jedis);
                }
            }
        }.runTaskAsynchronously(KingdomDefense.getInstance());
    }
}

