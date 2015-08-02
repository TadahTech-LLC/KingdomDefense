package com.tadahtech.fadecloud.kd.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * @author Timothy Andis
 */
public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String name = event.getInventory().getName();
        Player player = (Player) event.getWhoClicked();
        Menu gui = Menu.get(player.getUniqueId());
        if (gui == null) {
            return;
        }
        if(!gui.getName().equalsIgnoreCase(name)) {
            return;
        }
        Button button = gui.getButton(event.getRawSlot());
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        if (button == null) {
            return;
        }
        if (button.shouldEmptyClick()) {
            button.onClick();
        } else {
            button.onClick(player);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        Menu menu = Menu.remove(player.getUniqueId());
        String name = inventory.getName();
        if (menu == null) {
            return;
        }
        if(!menu.getName().equalsIgnoreCase(name)) {
            return;
        }
        menu.onClose(player);
    }

}
