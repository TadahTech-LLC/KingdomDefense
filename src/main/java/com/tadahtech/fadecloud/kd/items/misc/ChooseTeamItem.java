package com.tadahtech.fadecloud.kd.items.misc;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.ModSpecialItem;
import com.tadahtech.fadecloud.kd.menu.menus.TeamMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class ChooseTeamItem extends ModSpecialItem {

    public ChooseTeamItem() {
        super(new ItemBuilder(new ItemStack(Material.SKULL_ITEM))
          .name(ChatColor.AQUA + "Choose Team")
          .build());
    }

    @Override
    public void onClick(Player player) {
        new TeamMenu(KingdomDefense.getInstance().getGame().teams()).open(player);
    }

    @Override
    protected boolean alwaysAllow() {
        return true;
    }
}
