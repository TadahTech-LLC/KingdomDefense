package com.tadahtech.fadecloud.kd.io;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.listeners.SellListener;
import com.tadahtech.fadecloud.kd.shop.ShopItem;
import com.tadahtech.fadecloud.kd.shop.shops.GameShop;
import com.tadahtech.fadecloud.kd.shop.shops.KitShop;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class ShopIO {

    private File dir;
    private FileConfiguration config;

    public ShopIO() {
        this.dir = new File(KingdomDefense.getInstance().getDataFolder(), "shops.yml");
        if(!dir.exists()) {
            KingdomDefense.getInstance().saveResource("shops.yml", true);
        }
        File file = new File(KingdomDefense.getInstance().getDataFolder(), "item-prices.yml");
        if(!file.exists()) {
            KingdomDefense.getInstance().saveResource("item-prices.yml", true);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("prices");
        Map<Material, Integer> prices = Maps.newHashMap();
        for(String s : section.getKeys(false)) {
            Material material = Material.getMaterial(s.toUpperCase());
            int price = section.getInt(s, 1);
            prices.put(material, price);
        }
        new SellListener(prices);
        this.config = YamlConfiguration.loadConfiguration(dir);
        load();
    }

    private void load() {
        ConfigurationSection game = config.getConfigurationSection("game");
        ConfigurationSection kits = config.getConfigurationSection("kits");
        Map<CSKit, Integer> prices = Maps.newHashMap();
        if(kits != null) {
            for(String s : kits.getKeys(false)) {
                prices.put(CSKit.from(s), kits.getInt(s));
            }
            Location location = Utils.locFromString(kits.getString("location"));
            new KitShop(prices, location);
        }

        ConfigurationSection items = game.getConfigurationSection("items");
        List<ShopItem> shopItems = Lists.newArrayList();
        if(items != null) {
            for(String s : items.getKeys(false)) {
                ConfigurationSection section = items.getConfigurationSection(s);
                shopItems.add(ShopItem.from(section));
            }
        }
        new GameShop(shopItems);
    }

}
