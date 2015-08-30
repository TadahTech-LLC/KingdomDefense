package com.tadahtech.fadecloud.kd.items;

import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class HeadItems {

    public static ItemStack ZOMBIE, CREEPER, SKELETON, ENDERMAN;

    private static Map<String, ItemStack> CACHE = Maps.newHashMap();

    static {
        ZOMBIE = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).amount(1).data((byte) 2).build();
        CREEPER = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).amount(1).data((byte) 4).build();
        SKELETON = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).amount(1).data((byte) 3).setOwner("MHF_Skeleton").build();
        ENDERMAN = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).amount(1).data((byte) 3).setOwner("MHF_Enderman").build();
        CACHE.put("zombie", ZOMBIE);
        CACHE.put("skeleton", SKELETON);
        CACHE.put("enderman", ENDERMAN);
        CACHE.put("creeper", CREEPER);
    }

    public static ItemStack getItem(String name) {
        if(CACHE.containsKey(name.toLowerCase())) {
            return CACHE.get(name.toLowerCase());
        }
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(name);
        itemStack.setItemMeta(meta);
        CACHE.put(name.toLowerCase(), itemStack);
        return itemStack;
    }

}
