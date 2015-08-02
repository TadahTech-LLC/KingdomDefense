package com.tadahtech.fadecloud.kd.items;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis (TadahTech) on 8/1/2015.
 */
public class HubItem extends ModSpecialItem {

    public HubItem() {
        super(new ItemBuilder(new ItemStack(Material.SLIME_BALL))
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Click to return to the Hub")
          .build());
    }

    @Override
    public void onClick(Player player) {
        KingdomDefense.getInstance().redirect(KingdomDefense.getInstance().getHubServerName(), player);
    }

    @Override
    protected long getCooldown() {
        return 0;
    }
}
