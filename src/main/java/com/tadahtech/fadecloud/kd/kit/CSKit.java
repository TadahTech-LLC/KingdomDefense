package com.tadahtech.fadecloud.kd.kit;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
        if(!canUse(player)) {
            player.sendMessage(ChatColor.RED + "You cannot use this Kit right now!");
            return;
        }
        getItems().stream().forEach(item -> player.getInventory().addItem(item));
        player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Given the " + getName() + " kit.");
        player.closeInventory();
        cooldowns.putIfAbsent(player.getUniqueId(), System.currentTimeMillis());
    }

    public List<String> toLore() {
        List<String> lore = new ArrayList<>();
        for(String s : getDescription()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        lore.add(" ");
        lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "Contents: ");
        lore.add(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "-------------------");
        StringBuilder builder = new StringBuilder();
        for (ItemStack item : items) {
            String name = Utils.friendly(item.getType().name());
            if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                name = item.getItemMeta().getDisplayName();
            }
            int amount = item.getAmount();
            Map<Enchantment, Integer> enchantments = item.getEnchantments();
            String base = ChatColor.DARK_AQUA + friendly(item.getType());
            if(item.getDurability() > 0) {
                base = ChatColor.DARK_AQUA + dyeName((byte) item.getDurability()) +  friendly(item.getType());
            }
            base += ChatColor.AQUA + " x";
            base += ChatColor.DARK_AQUA.toString() + amount;
            builder.append(base).append(" ").append(ChatColor.WHITE).append("(").append(ChatColor.RESET).append(name).append(ChatColor.WHITE).append(")");
            lore.add(builder.toString());
            builder = new StringBuilder();
            if (enchantments != null && !enchantments.isEmpty()) {
                builder.append(ChatColor.RED).append("Enchantments: ");
                lore.add(builder.toString());
                builder = new StringBuilder();
                for (Enchantment enchantment : enchantments.keySet()) {
                    Integer level = enchantments.get(enchantment);
                    String enchantName = friendlyName(enchantment.getName());
                    enchantName = enchantName.substring(0, 1).toUpperCase() + enchantName.substring(1).toLowerCase();
                    builder.append(ChatColor.GREEN).append(enchantName).append(" ").append(ChatColor.RED).append(level);
                    lore.add(builder.toString());
                    builder = new StringBuilder();
                }
            }
        }
        return lore;
    }

    public boolean canUse(Player player) {
        if(cooldowns.get(player.getUniqueId()) == null) {
            return true;
        }
        long current = System.currentTimeMillis();
        long in = cooldowns.get(player.getUniqueId());
        boolean cd = TimeUnit.MINUTES.toSeconds(4) - ((current - in) / 1000) <= 0;
        if(cd) {
            cooldowns.remove(player.getUniqueId());
        }
        return cd;
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
