package com.tadahtech.fadecloud.kd.kit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class CSKit {

    private static Map<String, CSKit>  kits = Maps.newHashMap();

    private List<ItemStack> items;
    private String name;
    private String[] description;
    private static  Map<UUID, Long> cooldowns = Maps.newHashMap();

    public CSKit(List<ItemStack> items, String name, String... description) {
        this.items = items;
        this.name = name;
        this.description = description;
        kits.putIfAbsent(name, this);
    }

    public static Collection<CSKit> getAll() {
        return kits.values();
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public String[] getDescription() {
        return description;
    }

    public void give(Player player) {
        if(!hasPermission(player)) {
            player.sendMessage(ChatColor.RED + "You are not allowed to use this kit!!");
            return;
        }
        getItems().stream().forEach(item -> player.getInventory().addItem(item));
        if(!getName().equalsIgnoreCase("default")) {
            Lang.KIT_EQUIPPED.send(player, ImmutableMap.of("kit", getName()));
        }

        player.closeInventory();
        cooldowns.putIfAbsent(player.getUniqueId(), System.currentTimeMillis());
    }

    public List<String> toLore() {
        List<String> lore = Lists.newArrayList();
        for(String s : getDescription()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        lore.add(" ");
        lore.add(ChatColor.AQUA.toString() + "Contents: ");
        lore.add(" ");
        for (ItemStack item : items) {
            StringBuilder builder = new StringBuilder();
            ItemMeta meta = item.getItemMeta();
            Map<Enchantment, Integer> enchantments = meta.getEnchants();
            String base = ChatColor.AQUA + friendly(item.getType());
            if(item.getData().getData() > 0) {
                base = ChatColor.AQUA + dyeName(item.getData().getData()) +  friendly(item.getType());
            }
            builder.append(base);
            if(enchantments != null && !enchantments.isEmpty()) {
                builder.append(" ").append(ChatColor.DARK_GRAY).append("(");
                for (Enchantment enchantment : enchantments.keySet()) {
                    Integer level = enchantments.get(enchantment);
                    String enchantName = friendlyName(enchantment.getName());
                    enchantName = enchantName.substring(0, 1).toUpperCase() + enchantName.substring(1).toLowerCase();
                    builder.append(ChatColor.GRAY).append(enchantName).append(" ").append(ChatColor.GRAY).append(level);
                    builder.append(ChatColor.DARK_GRAY).append(")");
                    lore.add(builder.toString());
                }
            }
            lore.add(builder.toString());
        }
        return lore;
    }

    public boolean canUse(Player player) {
        return true;
    }

    private String dyeName(byte data) {
        return Utils.dyeName(data);
    }

    private String friendlyName(String name) {
        String enc = Utils.friendlyName(name);
        return Utils.friendly(enc);
    }

    private String friendly(Material material) {
        return Utils.friendly(material.name());
    }

    public static CSKit from(String s) {
        return kits.get(s);
    }

    public boolean hasPermission(Player bukkitPlayer) {
        return bukkitPlayer.hasPermission("kd.kits." + getName().toLowerCase());
    }

    public static CSKit getDefault() {
        return kits.get("Default");
    }
}
