package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Timothy Andis (TadahTech) on 7/29/2015.
 */
public class InfoListener implements Listener {

    @EventHandler
    public void onAsync(AsyncPlayerPreLoginEvent event) {
        KingdomDefense.getInstance().getInfoStore().load(event.getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().remove(player);
        KingdomDefense.getInstance().getInfoStore().save(info);
    }
}
