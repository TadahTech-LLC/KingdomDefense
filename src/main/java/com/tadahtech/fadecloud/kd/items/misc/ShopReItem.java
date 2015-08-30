package com.tadahtech.fadecloud.kd.items.misc;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.ModSpecialItem;
import com.tadahtech.fadecloud.kd.shop.menu.ShopMenu;
import com.tadahtech.fadecloud.kd.shop.shops.GameShop;
import com.tadahtech.fadecloud.kd.shop.shops.KitShop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class ShopReItem extends ModSpecialItem {

    public ShopReItem() {
        super(new ItemBuilder(new ItemStack(Material.EMERALD))
          .name(ChatColor.AQUA.toString() + "Shop")
          .build());
    }

    @Override
    public void onClick(Player player) {
        if(KingdomDefense.getInstance().getGame() != null) {
            //gameserver
            new ShopMenu(GameShop.INSTANCE).open(player);
            return;
        }
        KitShop.INSTANCE.getMenu(player).open(player);
    }

    @Override
    protected boolean alwaysAllow() {
        return true;
    }
}
