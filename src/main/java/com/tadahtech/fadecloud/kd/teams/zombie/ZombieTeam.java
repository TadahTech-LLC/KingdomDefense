package com.tadahtech.fadecloud.kd.teams.zombie;

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
        player.setMaxHealth(24.0);
        player.setWalkSpeed(0.1f);
        super.onRespawn(player);
    }

    @Override
    public void add(Player player) {
        player.setWalkSpeed(0.1f);
        player.setMaxHealth(24.0);
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
        String[] lore = {
          " ",
          ChatColor.GRAY + "Gruhhhh Brainzzz",
          ChatColor.GRAY + "Defend the King Zombie with the aid of the Unforgotten",
          ChatColor.GRAY + "Your strength is mightier than those of the living",
          ChatColor.GRAY + "You deal 0.5 x more damage per melee attack",
          ChatColor.GRAY + "But be warned, you move slower than the rest..",
          " ",
          ChatColor.RED + "Active Ability: ",
          ChatColor.GRAY + "Aid eof Anduril",
          ChatColor.GRAY + "Summon The Dead to aide your conquest",
          ChatColor.RED + "Passive Ability: ",
          ChatColor.GRAY + "Gladiator",
          ChatColor.GRAY + "Receive 2-8 more hearts.",
          ChatColor.GRAY + "But regenerate at 1/4 the normal speed",
          " ",
          ChatColor.RED + "Levels: 4",
          ChatColor.GREEN + "1-4 " + ChatColor.GRAY + "Undead Warriors",
          ChatColor.GREEN + "12-18 " + ChatColor.GRAY + "More hearts",
          ChatColor.GRAY + "Undead Warriors spawned is equal to your level",
          ChatColor.GRAY + "Max health increase is 2x your level + 10"
        };
        builder.lore(lore);
        return builder.build();
    }

    @Override
    public void tick() {

    }
}
