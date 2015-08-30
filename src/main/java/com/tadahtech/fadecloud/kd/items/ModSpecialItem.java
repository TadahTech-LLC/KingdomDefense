package com.tadahtech.fadecloud.kd.items;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.map.Island;
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
        if(alwaysAllow()) {
            this.onClick(player);
            this.use(player);
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
            return;
        }
        Game game = KingdomDefense.getInstance().getGame();
        if(game != null) {
            if(game.getState() != GameState.BATTLE) {
                player.sendMessage(Lang.PREFIX + "You can't use this during peace time!");
                return;
            }
            PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
            Island island = info.getCurrentTeam().getIsland();
            if(island.inCastle(player.getLocation())) {
                info.sendMessage(ChatColor.RED + "You can't do that in your castle!");
                return;
            }
        }
        if (!canUse(player)) {
            long time = 60 - (System.currentTimeMillis() - cooldowns.get(player.getUniqueId())) / 1000;
            String message = Lang.PREFIX + "You need to wait " + (time < 10 ? "0" + time : time ) + (time == 1 ? " second" : " seconds") + " before using this again";
            player.sendMessage(message);
            return;
        }
        this.onClick(player);
    }

    protected boolean alwaysAllow() {
        return false;
    }

    public abstract void onClick(Player player);

    public boolean canUse(Player player) {
        long curr = System.currentTimeMillis();
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return true;
        }
        long in = cooldowns.get(player.getUniqueId());
        long dif = (60) - (curr - in) / 1000;
        if (dif <= 0) {
            cooldowns.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

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
