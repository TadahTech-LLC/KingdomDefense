package com.tadahtech.fadecloud.kd.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.lang.Lang;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Timothy Andis
 */
public class ChestListener implements Listener {

    private Tier TIER_1;
    private Tier TIER_2;
    private Tier TIER_3;
    private final Random random = new Random();
    private Map<UUID, Entry<Location, Material>> types = Maps.newHashMap();

    public ChestListener(Tier tier, Tier tier2, Tier tier1) {
        this.TIER_1 = tier1;
        this.TIER_2 = tier2;
        this.TIER_3 = tier;
        KingdomDefense.getInstance().getServer().getPluginManager().registerEvents(this, KingdomDefense.getInstance());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        Location blockLoc = event.getBlock().getLocation();
        if(event.getBlock().getType() != Material.STONE) {
            return;
        }
        info.setBlocksBroken(info.getBlocksBroken() + 1);
        if(info.getBlocksBroken() < 20) {
            return;
        }
        if(info.getFoundChests() >= 3) {
            return;
        }
        double rand = random.nextInt(100);
        double t1 = TIER_1.getChance();
        double t2 = TIER_2.getChance();
        double t3 = TIER_3.getChance();
        List<ItemStack> t1items = Lists.newArrayList(TIER_1.getItems());
        List<ItemStack> t2items = Lists.newArrayList(TIER_2.getItems());
        List<ItemStack> t3items = Lists.newArrayList(TIER_3.getItems());
        Inventory inventory = Bukkit.createInventory(player, 27);
        if(rand <= t3) {
            for(int i = 0; i < random.nextInt(TIER_3.getMaxItems()) + 1; i++) {
                ItemStack itemStack = t3items.get(random.nextInt(t3items.size()));
                int slot = random.nextInt(27);
                if(inventory.getItem(slot) != null) {
                    slot = inventory.firstEmpty();
                }
                inventory.setItem(slot, itemStack);
            }
        } else if(rand <= t2) {
            for(int i = 0; i < random.nextInt(TIER_2.getMaxItems()) + 1; i++) {
                ItemStack itemStack = t2items.get(random.nextInt(t2items.size()));
                int slot = random.nextInt(27);
                if(inventory.getItem(slot) != null) {
                    slot = inventory.firstEmpty();
                }
                inventory.setItem(slot, itemStack);
            }
        } else if(rand <= t1) {
            for(int i = 0; i < random.nextInt(TIER_1.getMaxItems()) + 1; i++) {
                ItemStack itemStack = t1items.get(random.nextInt(t1items.size()));
                int slot = random.nextInt(27);
                if(inventory.getItem(slot) != null) {
                    slot = inventory.firstEmpty();
                }
                inventory.setItem(slot, itemStack);
            }
        } else {
            return;
        }
        Block down = blockLoc.getBlock().getRelative(BlockFace.DOWN);
        Material old = down.getType();
        down.setType(Material.CHEST);
        down.setMetadata("loot", new FixedMetadataValue(KingdomDefense.getInstance(), inventory));
        Entry<Location, Material> map = new SimpleEntry<>(down.getLocation(), old);
        types.put(player.getUniqueId(), map);
        Lang.CHEST_FOUND.send(player);
        info.setFoundChests(info.getFoundChests() + 1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        down.getLocation().getWorld().playEffect(down.getLocation(), Effect.STEP_SOUND, down.getType());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if(block == null) {
            return;
        }
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if(block.getType() != Material.CHEST) {
            return;
        }
        if(!block.hasMetadata("loot")) {
            return;
        }
        event.setCancelled(true);
        event.setUseInteractedBlock(Result.DENY);
        Inventory inventory = (Inventory) block.getMetadata("loot").get(0).value();
        Inventory newInv = Bukkit.createInventory(player, 27, "Loot Chest");
        for(int i = 0; i < 27; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            newInv.setItem(i, itemStack);
        }
        block.removeMetadata("loot", KingdomDefense.getInstance());
        player.openInventory(newInv);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if(!types.containsKey(player.getUniqueId())) {
            return;
        }
        if(!event.getInventory().getName().equalsIgnoreCase("Loot Chest")) {
            return;
        }
        Entry<Location, Material> map = types.remove(player.getUniqueId());
        map.getKey().getBlock().setType(map.getValue());
        map.getKey().getWorld().playEffect(map.getKey(), Effect.STEP_SOUND, map.getValue());
        player.playEffect(map.getKey(), Effect.EXPLOSION, 0.0D);
        player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F);
    }

    public static class Tier {

        private double chance;
        private int maxItems;
        private List<ItemStack> items;

        public Tier(double chance, int maxItems, List<ItemStack> items) {
            this.chance = chance;
            this.maxItems = maxItems;
            this.items = items;
        }

        public double getChance() {
            return chance;
        }

        public int getMaxItems() {
            return maxItems;
        }

        public List<ItemStack> getItems() {
            return items;
        }
    }
}
