package com.tadahtech.fadecloud.kd.teams;

import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.ModSpecialItem;
import com.tadahtech.fadecloud.kd.items.WrappedEnchantment;
import com.tadahtech.fadecloud.kd.teams.creeper.CreeperItem;
import com.tadahtech.fadecloud.kd.teams.enderman.EndermanItem;
import com.tadahtech.fadecloud.kd.teams.skeleton.SkeletonItem;
import com.tadahtech.fadecloud.kd.teams.zombie.ZombieItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class Loadout {


    private ItemStack[] items;
    private ModSpecialItem[] specialItems;

    public Loadout(ItemStack[] items, ModSpecialItem[] specialItems) {
        this.items = items;
        this.specialItems = specialItems;
    }

    public void load(Player player) {
        for (ItemStack itemStack : items) {
            player.getInventory().addItem(itemStack);
            player.updateInventory();
        }
        for (ModSpecialItem item : specialItems) {
            item.give(player, 8);
        }
    }

    public static final Loadout CREEPER = new Loadout(new ItemStack[]{
      new ItemStack(Material.STONE_SWORD),
      new ItemStack(Material.FLINT_AND_STEEL),
      new ItemStack(Material.TNT, 4)}, new ModSpecialItem[]{new CreeperItem()});

    public static final Loadout ZOMBIE = new Loadout(new ItemStack[]{
      new ItemStack(Material.IRON_SWORD)}, new ModSpecialItem[]{new ZombieItem()});

    public static final Loadout SKELETON = new Loadout(new ItemStack[]{
      new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 12)}, new ModSpecialItem[]{new SkeletonItem()});

    public static final Loadout ENDERMAN = new Loadout(new ItemStack[]{
      ItemBuilder.wrap(new ItemStack(Material.STICK))
      .enchant(new WrappedEnchantment(Enchantment.DAMAGE_ALL, 2))
      .build()
    }, new ModSpecialItem[]{new EndermanItem()});
}
