package com.tadahtech.fadecloud.kd.shop.shops;

import com.tadahtech.fadecloud.kd.shop.Shop;
import com.tadahtech.fadecloud.kd.shop.ShopItem;
import net.md_5.bungee.api.ChatColor;

import java.util.List;

/**
 * Created by Timothy Andis
 */
public class GameShop extends Shop {

    public static GameShop INSTANCE;

    public GameShop(List<ShopItem> shopItems) {
        super(shopItems, ChatColor.DARK_PURPLE + "Shop", null);
        if(INSTANCE == null) {
            INSTANCE = this;
        }
    }
}
