package com.tadahtech.fadecloud.kd.teams;

import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.WrappedEnchantment;
import com.tadahtech.fadecloud.kd.teams.creeper.CreeperItem;
import com.tadahtech.fadecloud.kd.teams.enderman.EndermanItem;
import com.tadahtech.fadecloud.kd.teams.skeleton.SkeletonItem;
import com.tadahtech.fadecloud.kd.teams.zombie.ZombieItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class Loadout {


    private ItemStack[] items, armor;
    private ModSpecialItem[] specialItems;

    public Loadout(ItemStack[] items, ItemStack[] armor, ModSpecialItem[] specialItems) {
        this.items = items;
        this.specialItems = specialItems;
        this.armor = armor;
    }

    public void load(Player player) {
        for (ItemStack itemStack : items) {
            player.getInventory().addItem(itemStack);
            player.updateInventory();
        }
        for (ModSpecialItem item : specialItems) {
            item.give(player, 8);
        }
        player.getInventory().setBoots(armor[0]);
        player.getInventory().setLeggings(armor[1]);
        player.getInventory().setChestplate(armor[2]);
        player.getInventory().setHelmet(armor[3]);
    }

    public static final Loadout CREEPER = new Loadout(new ItemStack[]{
      new ItemStack(Material.STONE_SWORD),
      new ItemStack(Material.FLINT_AND_STEEL),
      new ItemStack(Material.TNT, 4)}, new ItemStack[]{
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_BOOTS)).color(Color.LIME).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_LEGGINGS)).color(Color.LIME).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_CHESTPLATE)).color(Color.LIME).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_HELMET)).color(Color.LIME).build(),
    }, new ModSpecialItem[]{new CreeperItem()});

    public static final Loadout ZOMBIE = new Loadout(new ItemStack[]{
      new ItemStack(Material.IRON_SWORD)}, new ItemStack[]{
      new ItemStack(Material.IRON_BOOTS),
      new ItemStack(Material.IRON_LEGGINGS),
      new ItemStack(Material.IRON_CHESTPLATE),
      new ItemStack(Material.IRON_HELMET)
    }, new ModSpecialItem[]{new ZombieItem()});

    public static final Loadout SKELETON = new Loadout(new ItemStack[]{
      new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 12)}, new ItemStack[]{
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_BOOTS)).color(Color.WHITE).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_LEGGINGS)).color(Color.WHITE).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_CHESTPLATE)).color(Color.WHITE).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_HELMET)).color(Color.WHITE).build(),
    }, new ModSpecialItem[]{new SkeletonItem()});

    public static final Loadout ENDERMAN = new Loadout(new ItemStack[]{
      ItemBuilder.wrap(new ItemStack(Material.STICK))
      .enchant(new WrappedEnchantment(Enchantment.DAMAGE_ALL, 2))
      .build()
    }, new ItemStack[]{
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_BOOTS))
        .enchant(new WrappedEnchantment(Enchantment.PROTECTION_FALL, 3))
        .color(Color.BLACK).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_LEGGINGS)).color(Color.BLACK).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_CHESTPLATE)).color(Color.BLACK).build(),
      ItemBuilder.wrap(new ItemStack(Material.LEATHER_HELMET)).color(Color.BLACK).build(),
    }, new ModSpecialItem[]{new EndermanItem()});
}
