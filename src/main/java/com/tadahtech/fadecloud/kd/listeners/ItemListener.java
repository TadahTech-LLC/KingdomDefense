package com.tadahtech.fadecloud.kd.listeners;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Timothy Andis (TadahTech) on 8/1/2015.
 */
public class ItemListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
        ItemStack itemStack = player.getItemInHand();
        if(itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }
        ModSpecialItem specialItem = ModSpecialItem.get(itemStack);
        if(specialItem == null) {
            return;
        }
        specialItem.onInteract(event);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if(itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }
        ModSpecialItem specialItem = ModSpecialItem.get(itemStack);
        if(specialItem == null) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = Lists.newArrayList(event.getDrops());
        for(ItemStack itemStack : drops) {
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                return;
            }
            ModSpecialItem specialItem = ModSpecialItem.get(itemStack);
            if(specialItem == null) {
                return;
            }
            event.getDrops().remove(itemStack);
        }
    }
}
