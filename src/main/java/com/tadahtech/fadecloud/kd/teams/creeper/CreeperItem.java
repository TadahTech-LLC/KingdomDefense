package com.tadahtech.fadecloud.kd.teams.creeper;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * Created by Timothy Andis
 */
public class CreeperItem extends ModSpecialItem {

    public CreeperItem() {
        super(ItemBuilder.wrap(new ItemStack(Material.SKULL_ITEM, 1, (byte) 4))
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Self Destruct")
          .lore(" ", ChatColor.GRAY + "Right click to summon TNT around you.")
          .build());
    }

    @Override
    public void onClick(Player player) {
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if(info == null) {
            return;
        }
        int level = info.getLevel(TeamType.CREEPER);
        int tickTime = 24;
        for(int i = 0; i < level; i++) {
            tickTime -= 2;
        }
        Location location = player.getEyeLocation();
        World world = player.getWorld();
        List<Location> circle = Utils.circle(location, 3, 1, true, false, 0);
        for(int i = 0; i < level; i++) {
            TNTPrimed tnt = world.spawn(circle.get(i), TNTPrimed.class);
            tnt.setFuseTicks(tickTime);
            tnt.setMetadata("creeper", new FixedMetadataValue(KingdomDefense.getInstance(), level));
        }
    }

    @Override
    protected long getCooldown() {
        return 120;
    }
}
