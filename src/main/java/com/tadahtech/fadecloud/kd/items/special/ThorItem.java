package com.tadahtech.fadecloud.kd.items.special;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.ModSpecialItem;
import com.tadahtech.fadecloud.kd.items.WrappedEnchantment;
import com.tadahtech.fadecloud.kd.utils.CustomFirework;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class ThorItem extends ModSpecialItem {

    private PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS, 20 * 7, 5, true);
    private PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOW, 20 * 7, 5, true);

    public ThorItem() {
        super(new ItemBuilder(new ItemStack(Material.DIAMOND_AXE))
          .name(ChatColor.AQUA.toString() + ChatColor.BOLD + "Thor's Hammer")
          .enchant(new WrappedEnchantment(Enchantment.DAMAGE_ALL, 1))
          .build());
    }

    @Override
    public void onClick(Player player) {
        FireworkEffect effect = FireworkEffect.builder().
          withColor(Color.AQUA)
          .with(FireworkEffect.Type.BALL_LARGE)
          .withFade(Color.BLACK)
          .withFlicker().build();

        World world = player.getWorld();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        List<Location> circle = Utils.circle(player.getLocation(), 8, 1, true, false, 10);
        player.sendMessage(ChatColor.GRAY + "Unleashing the power of the storm...");
        new BukkitRunnable() {

            private int i = 0;
            private int total = circle.size() - 1;

            @Override
            public void run() {
                Location location = circle.get(i);
                CustomFirework.spawn(location, effect);
                i++;
                if(i == total) {
                    circle.stream().forEach(loc -> CustomFirework.spawn(loc, effect));
                    cancel();
                    List<Player> players = player.getNearbyEntities(10, 10, 10).stream().
                      filter(e -> e instanceof Player)
                      .map(e -> (Player) e)
                      .filter(p -> !p.equals(player)).collect(Collectors.toList());
                    players.forEach(p -> {
                        p.damage(10);
                        if(p.getHealth() <= 0) {
                            info.setCoins(info.getCoins() + 20);
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                        }
                        p.addPotionEffect(BLINDNESS);
                        p.addPotionEffect(SLOWNESS);
                        p.setFireTicks(40);
                        world.strikeLightningEffect(p.getLocation());
                    });

                }
            }
        }.runTaskTimer(KingdomDefense.getInstance(), 0L, 1L);
    }


}
