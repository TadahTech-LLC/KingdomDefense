package com.tadahtech.fadecloud.kd.teams;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Timothy Andis
 */
public abstract class ModSpecialItem {

    public static final String PREFIX = ChatColor.GRAY.toString() + ChatColor.BOLD + "[" + ChatColor.AQUA + "Castle Siege" + ChatColor.GRAY + ChatColor.BOLD + "] " + ChatColor.YELLOW;

    private Map<UUID, Long> cooldowns = new HashMap<>();
    protected ItemStack itemStack;
    public static List<ModSpecialItem> ALL = new ArrayList<>();
    private static Map<ItemStack, ModSpecialItem> map = new HashMap<>();

    public ModSpecialItem(ItemStack item) {
        this.itemStack = item;
        this.cooldowns = new HashMap<>();
        if (ALL.contains(this)) {
            return;
        }
        ALL.add(this);
        map.putIfAbsent(item, this);
    }

    public void setItem(ItemStack item) {
        this.itemStack = item;
        map.remove(item);
        map.put(item, this);
    }

    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        if (playerInteractEvent.getAction().name().contains("LEFT")) {
            return;
        }
        if (!canUse(player)) {
            player.sendMessage(PREFIX + "I'm still cooling down!");
            return;
        }
        this.onClick(player);
        ItemStack itemStack = player.getItemInHand();
        if (itemStack == null) {
            return;
        }
        short dur = itemStack.getDurability();
        if (dur == 0) {
            return;
        }
        itemStack.setDurability((short) 0);
        player.setItemInHand(itemStack);
    }

    public abstract void onClick(Player player);

    public boolean canUse(Player player) {
        long curr = System.currentTimeMillis();
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return true;
        }
        long in = cooldowns.get(player.getUniqueId());
        long dif = (getCooldown()) - (curr - in) / 1000;
        if (dif <= 0) {
            cooldowns.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    protected abstract long getCooldown();

    public void use(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public void register(Listener listener) {
        KingdomDefense.getInstance().getServer().getPluginManager().registerEvents(listener, KingdomDefense.getInstance());
    }

    public static ModSpecialItem get(ItemStack itemStack) {
        return map.get(itemStack);
    }

    public void give(Player player, int i) {
        player.getInventory().setItem(i, itemStack);
        player.updateInventory();
    }
}
