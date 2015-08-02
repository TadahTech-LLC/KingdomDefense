package com.tadahtech.fadecloud.kd.teams.enderman;

import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.LocationType;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.Loadout;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * Created by Timothy Andis
 */
public class EndermanTeam extends CSTeam {

    private int ran = 0;
    private Random random = new Random();
    protected static PotionEffect EFFECT = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 10, 3);

    public EndermanTeam(Island island) {
        super(TeamType.ENDERMAN, island, Loadout.ENDERMAN);
    }

    public void onMove(PlayerInfo info) {
        if(info.isInvisible() || info.isInvisibleFromChance()) {
            Player player = info.getBukkitPlayer();
            Location location = player.getLocation();
            for(int i = 0; i < 10; i++) {
                location.getWorld().playEffect(location, Effect.SMOKE, Effect.SMOKE.getData());
            }
        }
    }

    @Override
    public void onOtherDamage(EntityDamageByEntityEvent event, PlayerInfo info) {
        Entity damagerRaw = event.getDamager();
        if(!(damagerRaw instanceof EnderPearl)) {
            return;
        }
        int damageReduc = (info.getLevel(TeamType.ENDERMAN) * 10) + 60;
        event.setDamage(event.getFinalDamage() / damageReduc);
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event, PlayerInfo info) {
        int next = random.nextInt(100);
        if(next <= 8) {
            info.getBukkitPlayer().addPotionEffect(EFFECT);
            info.setInvisibleFromChance(true);
            info.getBukkitPlayer().playSound(info.getBukkitPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 2.0F, 1.0F);
        }
    }

    @Override
    public void onHit(EntityDamageByEntityEvent event, PlayerInfo info) {
        if(info.isInvisible()) {
            Player player = info.getBukkitPlayer();
            if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                return;
            }
            event.setCancelled(true);
            return;
        }
        if(info.isInvisibleFromChance()) {
            event.setCancelled(true);
        }
    }

    @Override
    public void add(Player player) {

    }

    @Override
    public LocationType getLocationType() {
        return LocationType.ENDERMAN_KING;
    }

    @Override
    public ItemStack getMenuIcon() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
        ItemBuilder builder = new ItemBuilder(itemStack);
        builder.data((byte) 3);
        builder.amount(getSize());
        builder.name(ChatColor.LIGHT_PURPLE + "Enderman Team");
        String[] lore = {
          " ",
          ChatColor.GRAY + "-Vloop-",
          ChatColor.GRAY + "Defend the King Enderman with extreme trickiness",
          ChatColor.GRAY + "There is an 8% chance to become invisible when hit",
          ChatColor.GRAY + "However, you cannot attack while you're invisible from an attack.",
          ChatColor.GRAY + "But be warned, you take damage while in water.",
          " ",
          ChatColor.RED + "Active Ability: ",
          ChatColor.GRAY + "Ninja",
          ChatColor.GRAY + "Become Invisible for 10-18 seconds",
          ChatColor.GRAY + "You can attack during this, but only with fists.",
          ChatColor.RED + "Passive Ability: ",
          ChatColor.GRAY + "Ender Expert",
          ChatColor.GRAY + "Take 60%-100% less damage from explosions.",
          " ",
          ChatColor.RED + "Levels: 4",
          ChatColor.GREEN + "10-18 " + ChatColor.GRAY + "Second duration for Ninja",
          ChatColor.GRAY + "Duration is 10 + (your level X 2)",
          ChatColor.GRAY + "Damage is 60% + (10% X your level)",
        };
        builder.lore(lore);
        itemStack = builder.build();
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner("MHF_Enderman");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void tick() {
        ran++;
        if (ran != 2) {
            return;
        } else {
            ran = 0;
        }
        getBukkitPlayers().stream().forEach(player -> {
            for (int y = 0; y < 2; y++) {
                Location clone = player.getLocation().clone().add(0, y, 0);
                if (y == 1) {
                    Location other = player.getLocation().clone().subtract(0, y, 0 );
                    Material type = other.getBlock().getType();
                    if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                        player.damage(1);
                        player.sendMessage(ChatColor.GRAY + "Get out of the Water!");
                        player.playSound(player.getLocation(), Sound.ENDERMAN_HIT, 1.0f, 1.0f);
                    }
                    continue;
                }
                Material type = clone.getBlock().getType();
                if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                    player.damage(1);
                    player.sendMessage(ChatColor.GRAY + "Get out of the Water!");
                    player.playSound(player.getLocation(), Sound.ENDERMAN_HIT, 1.0f, 1.0f);
                }
            }
        });
    }
}
