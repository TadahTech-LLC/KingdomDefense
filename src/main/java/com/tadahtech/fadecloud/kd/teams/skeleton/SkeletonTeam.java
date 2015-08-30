package com.tadahtech.fadecloud.kd.teams.skeleton;

import com.google.common.collect.Lists;
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

import java.util.List;
import java.util.stream.Collectors;

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
    public void applyEffects(Player player) {

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
        List<String> loreRaw = KingdomDefense.getInstance().getConfig().getStringList("skeleton-desc");
        List<String> lore = Lists.newArrayList();
        lore.addAll(loreRaw.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        builder.lore(lore.toArray(new String[lore.size()]));
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
