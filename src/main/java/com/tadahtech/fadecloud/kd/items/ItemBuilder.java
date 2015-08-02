package com.tadahtech.fadecloud.kd.items;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Timothy Andis
 */
public class ItemBuilder {

    private ItemStack item;
    private int amount = 1;
    private String name;
    private String[] lore;
    private Color color;
    private boolean glow;
    private WrappedEnchantment[] enchantments;
    private byte data;
    private String owner;

    public ItemBuilder(ItemStack itemStack) {
        this.item = itemStack;
    }

    /**
     * Wrap an itemstack for editing
     *
     * @param item The desired item
     * @return Wrapped item
     */
    public static ItemBuilder wrap(ItemStack item) {
        return new ItemBuilder(item);
    }

    private void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * Add a name to the itemstack
     *
     * @param name The desired name
     */
    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Add lore to the itemstack dynamically
     *
     * @param lore The desired lore
     */
    public ItemBuilder lore(String... lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Apply enchantments to the item
     *
     * @param enchantments An array of WrappedEnchantment to be applied on building
     */
    public ItemBuilder enchant(WrappedEnchantment... enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    /**
     * Apply a color to the item.
     *
     * @param color The desired color
     */
    public ItemBuilder color(Color color) {
        if (!item.getType().name().toLowerCase().contains("leather_")) {
            System.out.println("ItemBuilder: Tried setting color to a non leather material. Ignoring");
            return this;
        }
        this.color = color;
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public ItemBuilder amount(int i) {
        this.amount = i;
        return this;
    }

    public ItemBuilder data(byte data) {
        this.data = data;
        return this;
    }

    /**
     * Built the item
     *
     * @return the new item stack
     */
    public ItemStack build() {
        ItemStack item = new ItemStack(this.item.getType(), amount, data);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            StringBuilder builder = new StringBuilder();
            name = name.replace("_", " ");
            builder.append(ChatColor.translateAlternateColorCodes('&', name));
            meta.setDisplayName(builder.toString());
        }
        if (lore != null) {
            List<String> lore = new ArrayList<>();
            for (String s : this.lore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(lore);
        }
        if (enchantments != null) {
            for (WrappedEnchantment enchantment : enchantments) {
                if(enchantment.getLevel() > enchantment.getEnchantment().getMaxLevel()) {
                    item.addUnsafeEnchantment(enchantment.getEnchantment(), enchantment.getLevel());
                } else {
                    meta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), true);
                }
            }
        }
        item.setItemMeta(meta);
        if (enchantments != null) {
            for (WrappedEnchantment enchantment : enchantments) {
                if (enchantment.getLevel() > enchantment.getEnchantment().getMaxLevel()) {
                    item.addUnsafeEnchantment(enchantment.getEnchantment(), enchantment.getLevel());
                }
            }
        }
        if (owner != null) {
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            skullMeta.setOwner(owner);
            item.setItemMeta(skullMeta);
        }
        return item;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ItemBuilder: {");
        builder.append("\n");
        builder.append("type: ").append(item.getType().name());
        builder.append("\n");
        builder.append("amount: ").append(amount);
        builder.append("\n");
        builder.append("name: ").append(name);
        builder.append("\n");
        builder.append("lore: ");
        if (lore == null) {
            builder.append("null");
        } else {
            builder.append("[");
            for (int i = 0; i < lore.length; i++) {
                String s = lore[i];
                builder.append(s);
                if ((i + 1) != lore.length) {
                    builder.append(", ");
                }
            }
            builder.append("]");
        }
        builder.append("\n");
        builder.append("color: ").append(color == null ? "null" : "Red: " + color.getRed() + " Green: " + color.getGreen() + " Blue: " + color.getBlue());
        builder.append("\n");
        builder.append("enchantments: ");
        if (enchantments == null) {
            builder.append("null");
        } else {
            builder.append("\n");
            for (int i = 0; i < enchantments.length; i++) {
                WrappedEnchantment enchantment = enchantments[i];
                builder.append("  name: ").append(enchantment.getEnchantment().getName())
                  .append("\n")
                  .append("  level: ").append(enchantment.getLevel())
                  .append("\n")
                  .append("  override: ").append(enchantment.isOverride());
                if ((i + 1) != enchantments.length) {
                    builder.append("\n");
                }
            }
        }
        builder.append("\n");
        builder.append("}");
        return builder.toString();
    }


    public ItemBuilder setOwner(String owner) {
        this.owner = owner;
        return this;
    }
}

