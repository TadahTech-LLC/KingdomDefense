package com.tadahtech.fadecloud.kd.shop.menu;

import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import com.tadahtech.fadecloud.kd.shop.Shop;
import com.tadahtech.fadecloud.kd.shop.ShopItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class ShopMenu extends Menu {

    private Shop shop;

    public ShopMenu(Shop shop) {
        super(ChatColor.DARK_PURPLE + shop.getName());
        this.shop = shop;
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[((shop.getShopItems().size() + 8) / 9 * 9)];
        buttons = pane(buttons);
        for (int i = 0; i < shop.getShopItems().size(); i++) {
            ShopItem shopItem = shop.getShopItems().get(i);
            buttons[i] = shopItem.toButton();
        }
        return buttons;
    }

    @Override
    public void onClose(Player player) {

    }
}
