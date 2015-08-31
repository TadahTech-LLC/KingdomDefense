package com.tadahtech.fadecloud.kd.kit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        if(!hasPermission(player) && !getDefault().equals(this)) {
            player.sendMessage(ChatColor.RED + "You are not allowed to use this kit!!");
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);

        if(!getName().equalsIgnoreCase("default")) {
            if(info.getActiveKit() != null && info.getActiveKit().equals(this)) {
                Lang.KIT_ALREADY_CHOSEN.send(player, ImmutableMap.of("kit", getName()));
                return;
            } else if (info.getActiveKit() != null) {
                CSKit kit = info.getActiveKit();
                kit.getItems().forEach(itemStack -> player.getInventory().remove(itemStack));
            }
            info.setActiveKit(this);
            Lang.KIT_EQUIPPED.send(player, ImmutableMap.of("kit", getName()));
        }
        getItems().stream().forEach(item -> player.getInventory().addItem(item));
        player.closeInventory();
    }

    public List<String> toLore() {
        List<String> lore = Lists.newArrayList();
        for(String s : getDescription()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
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
