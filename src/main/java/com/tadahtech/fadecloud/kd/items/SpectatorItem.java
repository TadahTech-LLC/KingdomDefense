package com.tadahtech.fadecloud.kd.items;

import com.tadahtech.fadecloud.kd.menu.menus.SpectatorMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class SpectatorItem extends ModSpecialItem {

    public SpectatorItem() {
        super(new ItemBuilder(new ItemStack(Material.COMPASS))
          .name(ChatColor.AQUA + "Spectator Menu " + ChatColor.GRAY + "(Right Click)")
          .build());
    }

    @Override
    public void onClick(Player player) {
        new SpectatorMenu().open(player);
    }

    @Override
    protected boolean alwaysAllow() {
        return true;
    }
}
