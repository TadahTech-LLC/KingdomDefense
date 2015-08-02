package com.tadahtech.fadecloud.kd.info;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.sql.Callback;
import com.tadahtech.fadecloud.kd.sql.SQLManager;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Timothy Andis (TadahTech) on 7/29/2015.
 */
public class InfoManager {

    private Map<UUID, PlayerInfo> infoMap = Maps.newHashMap();
    private SQLManager sqlManager;

    public InfoManager(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public void put(PlayerInfo info) {
        infoMap.putIfAbsent(info.getUuid(), info);
    }

    public PlayerInfo get(UUID uuid) {
        Optional<PlayerInfo> maybeInfo = Optional.ofNullable(infoMap.get(uuid));
        if(maybeInfo.isPresent()) {
            return maybeInfo.get();
        }
        Callback<PlayerInfo> infoCallBack = sqlManager.load(uuid);
        return infoCallBack.get();
    }

    public PlayerInfo get(Player player) {
        //Check for delayed shit.
        Optional<PlayerInfo> maybeInfo = Optional.ofNullable(infoMap.get(player.getUniqueId()));
        if(!maybeInfo.isPresent()) {
            sqlManager.load(player.getUniqueId());
            return null;
        }
        return maybeInfo.get();
    }

    public PlayerInfo remove(Player player) {
        return infoMap.remove(player.getUniqueId());
    }
}
