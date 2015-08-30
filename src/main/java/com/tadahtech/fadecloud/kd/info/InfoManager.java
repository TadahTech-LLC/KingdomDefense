package com.tadahtech.fadecloud.kd.info;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
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
        infoMap.remove(info.getUuid());
        infoMap.putIfAbsent(info.getUuid(), info);
    }

    public PlayerInfo get(UUID uuid) {
        Optional<PlayerInfo> maybeInfo = Optional.ofNullable(infoMap.get(uuid));
        if (!maybeInfo.isPresent()) {
            KingdomDefense.getInstance().getInfoStore().load(uuid);
            return null;
        }
        return maybeInfo.get();
    }

    public PlayerInfo get(Player player) {
        return get(player.getUniqueId());
    }

    public PlayerInfo remove(Player player) {
        return infoMap.remove(player.getUniqueId());
    }

    public void clear() {
        infoMap.clear();
    }
}
