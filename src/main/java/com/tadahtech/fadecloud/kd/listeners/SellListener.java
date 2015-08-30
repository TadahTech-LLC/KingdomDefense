package com.tadahtech.fadecloud.kd.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.lang.Lang;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class SellListener implements Listener {

    private Map<Material, Integer> PRICES = Maps.newHashMap();

    public SellListener(Map<Material, Integer> prices) {
        this.PRICES.putAll(prices);
        KingdomDefense.getInstance().getServer().getPluginManager().registerEvents(this, KingdomDefense.getInstance());
    }

    @EventHandler
    public void onSell(InventoryCloseEvent event) {
        if(!event.getInventory().getName().equalsIgnoreCase("Sell Items")) {
            return;
        }
        Player player = (Player) event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        ItemStack[] items = event.getInventory().getContents();
        List<ItemStack> giveBack = Lists.newArrayList();
        int total = 0;
        int sold = 0;
        for(int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            int amount = itemStack.getAmount();
            Material material = itemStack.getType();
            if(PRICES.get(material) == null) {
                giveBack.add(itemStack);
                continue;
            }
            int price = PRICES.get(material);
            price *= amount;
            total += price;
            sold++;
        }
        info.sendMessage(Lang.PREFIX + "Sold " + sold + " items for a take of " + total + " coins!");
        if(giveBack.size() > 0) {
            info.sendMessage(Lang.PREFIX + "Couldn't sell " + giveBack.size() + " items. Take them back!");
            for(ItemStack itemStack : giveBack) {
                player.getInventory().addItem(itemStack);
            }
        }
        info.addCoins(total);
        player.updateInventory();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
    }
}
