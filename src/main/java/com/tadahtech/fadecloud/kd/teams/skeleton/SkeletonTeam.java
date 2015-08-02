package com.tadahtech.fadecloud.kd.teams.skeleton;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.LocationType;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.Loadout;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Timothy Andis (TadahTech) on 7/29/2015.
 */
public class SkeletonTeam extends CSTeam {

    private int ticks;

    public SkeletonTeam(Island island) {
        super(TeamType.SKELETON, island, Loadout.SKELETON);
    }

    @Override
    public void onOtherDamage(EntityDamageByEntityEvent event, PlayerInfo info) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Projectile)) {
            return;
        }
        double reduc = 0.5d * (info.getLevel(TeamType.SKELETON));
        event.setDamage(event.getFinalDamage() + (event.getFinalDamage() * reduc));
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event, PlayerInfo info) {

    }

    @Override
    public void onHit(EntityDamageByEntityEvent event, PlayerInfo info) {
        event.setDamage(event.getFinalDamage() + (event.getFinalDamage() * 0.2));
    }

    @Override
    public void add(Player player) {

    }

    @Override
    public LocationType getLocationType() {
        return LocationType.SKELETON_KING;
    }

    @Override
    public ItemStack getMenuIcon() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
        ItemBuilder builder = new ItemBuilder(itemStack);
        builder.data((byte) 3);
        builder.amount(getSize());
        builder.name(ChatColor.DARK_GRAY + "Skeleton Team");
        String[] lore = {
          " ",
          ChatColor.GRAY + "\"Nothing should be feared more than a well trained bowman\"",
          ChatColor.GRAY + "Defend the King Skeleton with ranged prowess",
          ChatColor.GRAY + "You are 12% stronger using ranged weapons",
          ChatColor.GRAY + "But be warned, you do 30% less damage using melee",
          " ",
          ChatColor.RED + "Active Ability: ",
          ChatColor.GRAY + "Arrow Storm",
          ChatColor.GRAY + "Rain arrows on your foes from the heavens",
          ChatColor.RED + "Passive Ability: ",
          ChatColor.GRAY + "Ranger",
          ChatColor.GRAY + "Take up to 20% less damage from ranged attacks.",
          ChatColor.GRAY + "Gain 1-4 Arrows (Max 64) every 10 seconds",
          " ",
          ChatColor.RED + "Levels: 4",
          ChatColor.GREEN + "5%-20% " + ChatColor.GRAY + "Less Damage from Ranged attacks",
          ChatColor.GREEN + "1-4 " + ChatColor.GRAY + "Arrows per 10 seconds",
          ChatColor.GRAY + "Damage Reduction is equal to 5% X your level",
          ChatColor.GRAY + "1 Arrow per level"
        };
        builder.lore(lore);
        itemStack = builder.build();
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner("MHF_Skeleton");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void tick() {
        if (ticks != 4) {
            ticks++;
            return;
        }
        ticks = 0;
        getBukkitPlayers().stream().forEach(player -> {
            PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
            int level = info.getLevel(TeamType.SKELETON);
            int i = 0;
            for (ItemStack is : player.getInventory().getContents()) {
                if (is.getType() == Material.ARROW) {
                    i = i + is.getAmount();
                }
            }
            if (i >= 64) {
                return;
            }
            ItemStack itemStack = new ItemStack(Material.ARROW, level);
            player.getInventory().addItem(itemStack);
            player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1.0F, 1.0F);
        });
    }
}
