package com.tadahtech.fadecloud.kd.info;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Timothy Andis (TadahTech) on 7/29/2015.
 */
public class InfoManager {

    private Map<UUID, PlayerInfo> infoMap = Maps.newHashMap();

    public InfoManager() {

    }

    public void put(PlayerInfo info) {
        infoMap.putIfAbsent(info.getUuid(), info);
    }

    public PlayerInfo get(UUID uuid) {
        Optional<PlayerInfo> maybeInfo = Optional.ofNullable(infoMap.get(uuid));
        if (!maybeInfo.isPresent()) {
            return null;

        }
        return maybeInfo.get();
    }

    public PlayerInfo get(Player player) {
        //Check for delayed shit
        return get(player.getUniqueId());
    }

    public PlayerInfo remove(Player player) {
        return infoMap.remove(player.getUniqueId());
    }

    public void remove(PlayerInfo info) {
    }
}
