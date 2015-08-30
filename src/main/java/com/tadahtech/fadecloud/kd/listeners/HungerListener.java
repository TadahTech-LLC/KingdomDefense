package com.tadahtech.fadecloud.kd.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by Timothy Andis
 */
public class HungerListener implements Listener {

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
