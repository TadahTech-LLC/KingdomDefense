package com.tadahtech.fadecloud.kd.teams.creeper;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.LocationType;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.Loadout;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class CreeperTeam extends CSTeam {

    public CreeperTeam(Island island) {
        super(TeamType.CREEPER, island, Loadout.CREEPER);
    }

    @Override
    public void onOtherDamage(EntityDamageByEntityEvent event, PlayerInfo info) {
        Entity entity = event.getDamager();
        if(!(entity instanceof Explosive)) {
            return;
        }
        Explosive tnt = (Explosive) entity;
        int level = info.getLevel(this.type);
        int chance = 20;
        double damageReduc = 40;
        for (int i = 0; i < level; i++) {
            damageReduc += 10;
            chance -= 5;
        }
        if(random.nextInt(100) <= chance) {
            if(!tnt.hasMetadata("creeper")) {
                return;
            }
            //sooooo unlucky.
            info.getBukkitPlayer().damage(info.getBukkitPlayer().getHealth());
            return;
        }
        damageReduc /= 100;
        double damage = event.getFinalDamage();
        event.setDamage(damage * damageReduc);
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event, PlayerInfo info) {

    }

    @Override
    public void onHit(EntityDamageByEntityEvent event, PlayerInfo info) {
        event.setDamage(event.getFinalDamage() + (event.getFinalDamage() * 0.3D));
    }

    @Override
    public void add(Player player) {
        player.setWalkSpeed(0.4f);
    }

    @Override
    public LocationType getLocationType() {
        return LocationType.CREEPER_KING;
    }

    @Override
    public ItemStack getMenuIcon() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
        ItemBuilder builder = new ItemBuilder(itemStack);
        builder.data((byte) 4);
        builder.amount(getSize());
        builder.name(ChatColor.GREEN + "Creeper Team");
        String[] lore = {
          " ",
          ChatColor.GRAY + "Hissssssss BOMB",
          ChatColor.GRAY + "Defend the King Creeper with explosive might",
          ChatColor.GRAY + "Become resistant, by up to 80%, of explosive damage",
          ChatColor.GRAY + "Move faster than all the rest",
          ChatColor.GRAY + "But be warned, you do 30% less damage per hit.",
          " ",
          ChatColor.RED + "Active Ability: ",
          ChatColor.GRAY + "Self Destruct",
          ChatColor.GRAY + "Summon a wall of TNT to surround you",
          ChatColor.GRAY + "Be careful! There's up to a 20% chance to end your life.",
          ChatColor.RED + "Passive Ability: ",
          ChatColor.GRAY + "Explosive Expert",
          ChatColor.GRAY + "Take 40%-80% less damage from explosions.",
          ChatColor.GRAY + "Damage is 40% + (10% X your level)",
          " ",
          ChatColor.RED + "Levels: 4",
          ChatColor.GREEN + "1-4 " + ChatColor.GRAY + "Block radius for Self Destruct",
          ChatColor.GREEN + "20%-5% " + ChatColor.GRAY + "Chance to die on Self Destruct",
          ChatColor.GRAY + "Radius is equal to your level",
          ChatColor.GRAY + "Chance is 20% divided by your level"
        };
        builder.lore(lore);
        return builder.build();
    }

    @Override
    public void tick() {

    }
}
