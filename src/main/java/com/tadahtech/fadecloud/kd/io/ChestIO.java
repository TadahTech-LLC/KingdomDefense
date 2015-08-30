package com.tadahtech.fadecloud.kd.io;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.listeners.ChestListener;
import com.tadahtech.fadecloud.kd.listeners.ChestListener.Tier;
import com.tadahtech.fadecloud.kd.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

/**
 * Created by Timothy Andis
 */
public class ChestIO {

    private File dir;
    private FileConfiguration config;

    public ChestIO() {
        this.dir = new File(KingdomDefense.getInstance().getDataFolder(), "chests.yml");
        if(!dir.exists()) {
            KingdomDefense.getInstance().saveResource("chests.yml", true);
        }
        this.config = YamlConfiguration.loadConfiguration(dir);
        load();
    }

    private void load() {
        ConfigurationSection tier_1 = config.getConfigurationSection("tier-1");
        ConfigurationSection tier_2 = config.getConfigurationSection("tier-2");
        ConfigurationSection tier_3 = config.getConfigurationSection("tier-3");
        int max = tier_1.getInt("maxItems", 2);
        double chance = tier_1.getDouble("chance", 5.0);
        List<ItemStack> items = Lists.newArrayList();
        for(String s : tier_1.getConfigurationSection("items").getKeys(false)) {
            ConfigurationSection section = tier_1.getConfigurationSection("items").getConfigurationSection(s);
            items.add(Utils.fromSection(section));
        }
        Tier tier1 = new Tier(chance, max, items);

        max = tier_2.getInt("maxItems", 2);
        chance = tier_2.getDouble("chance");
        items = Lists.newArrayList();
        for(String s : tier_2.getConfigurationSection("items").getKeys(false)) {
            ConfigurationSection section = tier_2.getConfigurationSection("items").getConfigurationSection(s);
            items.add(Utils.fromSection(section));
        }
        Tier tier2 = new Tier(chance, max, items);

        max = tier_3.getInt("maxItems", 2);
        chance = tier_3.getDouble("chance");
        items = Lists.newArrayList();
        for(String s : tier_3.getConfigurationSection("items").getKeys(false)) {
            ConfigurationSection section = tier_3.getConfigurationSection("items").getConfigurationSection(s);
            items.add(Utils.fromSection(section));
        }
        Tier tier3 = new Tier(chance, max, items);
        new ChestListener(tier3, tier2, tier1);
    }
}
