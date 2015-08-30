package com.tadahtech.fadecloud.kd.teams.zombie;

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
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class ZombieTeam extends CSTeam {

    public ZombieTeam(Island island) {
        super(TeamType.ZOMBIE, island, Loadout.ZOMBIE);
    }

    @Override
    public void onOtherDamage(EntityDamageByEntityEvent event, PlayerInfo info) {

    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event, PlayerInfo info) {

    }

    @Override
    public void onHit(EntityDamageByEntityEvent event, PlayerInfo info) {
        event.setDamage(event.getFinalDamage() +( event.getFinalDamage() * 0.5));
    }

    @Override
    public void onRespawn(Player player) {
        player.setWalkSpeed(0.1f);
        player.setMaxHealth(24.0);
        player.setHealth(24.0);
        super.onRespawn(player);
    }

    @Override
    public void applyEffects(Player player) {
        player.setWalkSpeed(0.1f);
        player.setMaxHealth(24.0);
        player.setHealth(24.0);
    }

    @Override
    public LocationType getLocationType() {
        return LocationType.ZOMBIE_KING;
    }

    @Override
    public ItemStack getMenuIcon() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
        ItemBuilder builder = new ItemBuilder(itemStack);
        builder.data((byte) 2);
        builder.amount(getSize());
        builder.name(ChatColor.DARK_GREEN + "Zombie Team");
        List<String> loreRaw = KingdomDefense.getInstance().getConfig().getStringList("zombie-desc");
        List<String> lore = Lists.newArrayList();
        lore.addAll(loreRaw.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        builder.lore(lore.toArray(new String[lore.size()]));
        return builder.build();
    }

    @Override
    public void tick() {

    }
}
