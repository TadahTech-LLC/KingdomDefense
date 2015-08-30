package com.tadahtech.fadecloud.kd.shop.menu;

import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import com.tadahtech.fadecloud.kd.shop.shops.GameShop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class ShopMainMenu extends Menu {

    private Inventory inventory;

    public ShopMainMenu() {
        super(ChatColor.DARK_PURPLE + "Shop");
        this.inventory = Bukkit.createInventory(null, 45, "Sell Items");
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[27];
        buttons = pane(buttons);
        ItemStack buy = new ItemBuilder(new ItemStack(Material.EMERALD))
          .name(ChatColor.AQUA + "Buy Items")
          .lore(ChatColor.GRAY + "Right Click to view the shop")
          .build();
        ItemStack sell = new ItemBuilder(new ItemStack(Material.REDSTONE))
          .name(ChatColor.AQUA + "Sell Items")
          .lore(ChatColor.GRAY + "Right click to sell items")
          .build();
        buttons[12] = new Button(buy, player -> {
           new ShopMenu(GameShop.INSTANCE).open(player);
        });
        buttons[14] = new Button(sell, player -> player.openInventory(inventory));
        return buttons;
    }
}
