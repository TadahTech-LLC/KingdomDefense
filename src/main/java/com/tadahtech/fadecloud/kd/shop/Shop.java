package com.tadahtech.fadecloud.kd.shop;

import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.nms.CustomEntityType;
import com.tadahtech.fadecloud.kd.nms.mobs.KDVillager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Andis
 */
public class Shop {

    private List<ShopItem> shopItems;
    private String name;
    private String permission;

    private static Map<String, Shop> shops = Maps.newHashMap();

    public Shop(List<ShopItem> shopItems, String name, String permission) {
        this.shopItems = shopItems;
        this.name = name;
        this.permission = permission;
        shops.put(name, this);
    }

    public List<ShopItem> getShopItems() {
        return shopItems;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public KDVillager init(Location location) {
        KDVillager villager = (KDVillager) CustomEntityType.VILLAGER.spawn(location);
        assert villager != null : "Villager is null, duh.";
        LivingEntity livingEntity = (LivingEntity) villager.getBukkitEntity();
        livingEntity.setCustomNameVisible(true);
        livingEntity.setCustomName(ChatColor.AQUA + name);
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCanPickupItems(false);
        return villager;
    }
}
