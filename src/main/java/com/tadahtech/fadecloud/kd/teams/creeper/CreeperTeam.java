package com.tadahtech.fadecloud.kd.teams.creeper;

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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

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
    public void applyEffects(Player player) {
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
        List<String> loreRaw = KingdomDefense.getInstance().getConfig().getStringList("creeper-desc");
        List<String> lore = Lists.newArrayList();
        lore.addAll(loreRaw.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
        builder.lore(lore.toArray(new String[lore.size()]));
        return builder.build();
    }

    @Override
    public void tick() {

    }
}
