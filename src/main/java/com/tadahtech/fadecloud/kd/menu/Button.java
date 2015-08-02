package com.tadahtech.fadecloud.kd.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Tim [calebbfmv]
 *         Created by Tim [calebbfmv] on 10/1/2014.
 */
public class Button {

    private ItemStack item;
    private ClickExecutor executor;
    private EmptyClickExecutor emptyClickExecutor;
    public static int counter = 0;

    public Button(ItemStack item, ClickExecutor executor) {
        this.item = item;
        this.executor = executor;
    }

    public Button(ItemStack item, EmptyClickExecutor clickExecutor) {
        this.item = item;
        this.emptyClickExecutor = clickExecutor;
    }

    public void onClick() {
        this.emptyClickExecutor.click();
    }

    public Button() {

    }

    public boolean shouldEmptyClick() {
        return emptyClickExecutor != null;
    }

    public void onClick(Player player) {
        executor.click(player);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public interface ClickExecutor {

        public void click(Player player);
    }

    public interface EmptyClickExecutor {

        public void click();
    }
}
