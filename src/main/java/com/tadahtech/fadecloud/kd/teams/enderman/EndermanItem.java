package com.tadahtech.fadecloud.kd.teams.enderman;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Created by Timothy Andis (TadahTech) on 7/29/2015.
 */
public class EndermanItem extends ModSpecialItem {

    public EndermanItem() {
        super(ItemBuilder.wrap(new ItemStack(Material.EYE_OF_ENDER, 1, (byte) 4))
          .name(ChatColor.GRAY.toString() + ChatColor.BOLD + "Vanish")
          .lore(" ", ChatColor.GRAY + "Vanish into Thin Air.")
          .build());
    }

    @Override
    public void onClick(Player player) {
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        info.setInvisible(true);
        PotionEffect effect =  new PotionEffect(PotionEffectType.INVISIBILITY, 20 * (info.getLevel(TeamType.ENDERMAN) * 2) + 10, 3);
        player.addPotionEffect(effect);
        Location location = player.getLocation();
        location.getWorld().playSound(location, Sound.ENDERMAN_TELEPORT, 3.4F, 1.0F);
        List<Location> circle = Utils.circle(location, 2, 3, true, false, 0);
        for(Location loc : circle) {
            for(int i = 0; i < 10; i++) {
                loc.getWorld().playEffect(loc, Effect.PARTICLE_SMOKE, 0);
            }
        }
    }

    @Override
    protected long getCooldown() {
        return 120;
    }
}
