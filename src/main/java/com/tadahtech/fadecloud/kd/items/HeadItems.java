package com.tadahtech.fadecloud.kd.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Timothy Andis
 */
public class HeadItems {

    public static ItemStack ZOMBIE, CREEPER, SKELETON, ENDERMAN;

    static {
        ZOMBIE = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).amount(1).data((byte) 2).build();
        CREEPER = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).amount(1).data((byte) 4).build();
        SKELETON = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).amount(1).data((byte) 3).setOwner("MHF_Skeleton").build();
        ENDERMAN = new ItemBuilder(new ItemStack(Material.SKULL_ITEM)).amount(1).data((byte) 3).setOwner("MHF_Enderman").build();
        SkullMeta meta = (SkullMeta) ENDERMAN.getItemMeta();
        meta.setOwner("MHF_Enderman");
        ENDERMAN.setItemMeta(meta);
    }

}
