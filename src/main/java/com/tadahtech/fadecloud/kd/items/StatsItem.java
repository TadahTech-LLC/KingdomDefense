package com.tadahtech.fadecloud.kd.items;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.menu.menus.StatMenu;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis (TadahTech) on 8/1/2015.
 */
public class StatsItem extends ModSpecialItem {

    private PlayerInfo info;

    public StatsItem(PlayerInfo info) {
        super(new ItemBuilder(new ItemStack(Material.SKULL_ITEM))
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Statistics")
          .setOwner(info.getBukkitPlayer().getName())
          .build());
        this.info = info;
    }

    @Override
    public void onClick(Player player) {
        new StatMenu(info).open(player);
    }

    @Override
    protected long getCooldown() {
        return 0;
    }
}
