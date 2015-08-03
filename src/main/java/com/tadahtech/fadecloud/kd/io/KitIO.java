package com.tadahtech.fadecloud.kd.io;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.CSKit;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.WrappedEnchantment;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class KitIO {

    private List<CSKit> kits = new ArrayList<>();
    private Map<String, CSKit> kitMap = new HashMap<>();
    private final File dir;

    public KitIO(KingdomDefense plugin) {
        this.dir = new File(plugin.getDataFolder(), "kits");
        dir.mkdirs();
        load();
    }

    public void load() {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = config.getConfigurationSection("items");
            List<String> desc = config.getStringList("description");
            String[] description = new String[desc.size()];
            for (int i = 0; i < desc.size(); i++) {
                String string = desc.get(i);
                description[i] = ChatColor.translateAlternateColorCodes('&', string);
            }
            if (section == null) {
                continue;
            }
            List<ItemStack> items = new ArrayList<>();
            for (String s : section.getKeys(false)) {
                Material material = Material.matchMaterial(section.getString(s + ".material"));
                if (material == null) {
                    continue;
                }
                int amount = section.getInt(s + ".amount", 1);
                byte data = (byte) section.getDouble(s + ".data", 0);
                ItemStack item = new ItemStack(material);
                List<String> lore = new ArrayList<>();
                List<String> list;
                if ((list = section.getStringList(s + ".lore")) != null) {
                    list.stream().forEach(string -> lore.add(ChatColor.translateAlternateColorCodes('&', string)));
                }
                ItemBuilder builder = ItemBuilder.wrap(item);
                builder.amount(amount);
                builder.data(data);
                String n = section.getString(s + ".name");
                builder.lore(lore.toArray(new String[lore.size()]));
                if(n != null) {
                    builder.name(ChatColor.translateAlternateColorCodes('&', n));
                }
                if (section.getStringList(s + ".enchants") != null) {
                    List<String> enchants = section.getStringList(s + ".enchants");
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
                if(section.getString(s + ".color") != null) {
                    String colorString = section.getString(s + ".color");
                    int r = Integer.parseInt(colorString.split(", ")[0]);
                    int g = Integer.parseInt(colorString.split(", ")[1]);
                    int b = Integer.parseInt(colorString.split(", ")[2]);
                    Color color = Color.fromRGB(r, g, b);
                    builder.color(color);
                }
                items.add(builder.build());
            }
            CSKit kit = new CSKit(items, name, description);
            kitMap.putIfAbsent(name, kit);
            kits.add(kit);
        }
    }

    public Map<String, CSKit> getKitMap() {
        return kitMap;
    }

    public List<CSKit> getKits() {
        return kits;
    }

}
