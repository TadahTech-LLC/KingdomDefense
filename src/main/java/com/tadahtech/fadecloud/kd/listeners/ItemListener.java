package com.tadahtech.fadecloud.kd.listeners;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.HeadItems;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.ModSpecialItem;
import com.tadahtech.fadecloud.kd.items.misc.ShopReItem;
import com.tadahtech.fadecloud.kd.menu.menus.PlayerMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

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
        if(itemStack.getType() == Material.SKULL_ITEM) {
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            if(meta.getOwner() != null) {
                if(meta.getOwner().equalsIgnoreCase(player.getName())) {
                    event.setCancelled(true);
                    new PlayerMenu(KingdomDefense.getInstance().getInfoManager().get(player)).open(player);
                    return;
                }
            }
        }
        ModSpecialItem specialItem = ModSpecialItem.get(itemStack);
        if(specialItem == null) {
            return;
        }
        event.setCancelled(true);
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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(event.getPlayer());
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = info.getBukkitPlayer();
                ItemStack first = player.getInventory().getItem(1);
                if(first != null && first.getType() == Material.SKULL_ITEM) {
                    return;
                }
                ItemBuilder builder = new ItemBuilder(HeadItems.getItem(player.getName()));
                builder.data((byte) 3);
                builder.name(ChatColor.AQUA.toString() + "Profile" + ChatColor.GRAY + " (Right Click)");
                builder.lore(ChatColor.GRAY + "Right click to view your profile");
                info.getBukkitPlayer().getInventory().setItem(1, builder.cloneBuild());
                new ShopReItem().give(info.getBukkitPlayer(), 2);
            }
        }.runTaskLater(KingdomDefense.getInstance(), 3l);
    }
}
