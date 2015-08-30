package com.tadahtech.fadecloud.kd.shop;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.WrappedEnchantment;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Timothy Andis
 */
public class ShopItem {
    
    private ItemStack itemStack;
    private int price;
    
    public ShopItem(ItemStack itemStack, int price) {
        this.itemStack = itemStack;
        this.price = price;
    }
    
    public Button toButton() {
        return new Button(itemStack, player -> {
            PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
            if(info == null) {
                return;
            }
            if(!info.hasEnough(price)) {
                Lang.NOT_ENOUGH_COINS.send(info, ImmutableMap.of("total", String.valueOf(price), "remainder", String.valueOf((price - info.getCoins()))));
                player.closeInventory();
                return;
            }
            info.remove(price);
            Lang.ITEM_BOUGHT.send(info, ImmutableMap.of("item", Utils.pretty(itemStack.getType().name()), "price", String.valueOf(price)));
            player.getInventory().addItem(itemStack);
            player.closeInventory();
            player.updateInventory();
        });
    }
    
    public static ShopItem from(ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material"));
        if (material == null) {
            return null;
        }
        int amount = section.getInt("amount", 1);
        byte data = (byte) section.getDouble("data", 0);
        ItemStack item = new ItemStack(material);
        List<String> lore = Lists.newArrayList();
        List<String> list;
        if ((list = section.getStringList("lore")) != null) {
            list.stream().forEach(string -> lore.add(ChatColor.translateAlternateColorCodes('&', string)));
        }
        ItemBuilder builder = new ItemBuilder(item);
        builder.amount(amount);
        builder.data(data);
        String n = section.getString("name");
        builder.lore(lore.toArray(new String[lore.size()]));
        if(n != null) {
            builder.name(ChatColor.translateAlternateColorCodes('&', n));
        }
        if (section.getStringList("enchants") != null) {
            List<String> enchants = section.getStringList("enchants");
            WrappedEnchantment[] enchantments = new WrappedEnchantment[enchants.size()];
            for (int i = 0; i < enchantments.length; i++) {
                String string = enchants.get(i);
                String[] str = string.split(":");
                String enchantName = str[0];
                int level = Integer.parseInt(str[1]);
                Enchantment enchantment;
                try {
                    enchantment = Enchantment.getByName(enchantName);
                } catch (Exception e) {
                    enchantment = Utils.getEnchantmentName(enchantName);
                }
                if (enchantment != null) {
                    enchantments[i] = new WrappedEnchantment(enchantment, level);
                }
            }
            builder.enchant(enchantments);
        }
        if(section.getString("color") != null) {
            String colorString = section.getString("color");
            int r = Integer.parseInt(colorString.split(", ")[0]);
            int g = Integer.parseInt(colorString.split(", ")[1]);
            int b = Integer.parseInt(colorString.split(", ")[2]);
            Color color = Color.fromRGB(r, g, b);
            builder.color(color);
        }
        int price = section.getInt("price");
        return new ShopItem(builder.build(), price);
    }
    
}

