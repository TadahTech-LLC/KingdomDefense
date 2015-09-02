package com.tadahtech.fadecloud.kd.teams.zombie;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.items.ModSpecialItem;
import com.tadahtech.fadecloud.kd.nms.mobs.AttackZombie;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.threads.AIThread;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_7_R4.EntityZombie;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Timothy Andis
 */
public class ZombieItem extends ModSpecialItem {

    public ZombieItem() {
        super(ItemBuilder.wrap(new ItemStack(Material.SKULL_ITEM))
          .data((byte) 2)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Undead Warrior")
          .lore(" ", ChatColor.GRAY + "Summon 1-4 Zombies to fight nearest enemies",
            ChatColor.GRAY + "Lasts for 30 seconds.")
          .build());
    }

    @Override
    public void onClick(Player player) {
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if (info == null) {
            return;
        }
        int level = info.getLevel(TeamType.CREEPER);
        for (int i = 0; i < level; i++) {
            AttackZombie attackZombie = new AttackZombie(info);
            EntityZombie zombie = (EntityZombie) attackZombie.get();
            new BukkitRunnable() {
                @Override
                public void run() {
                    zombie.getBukkitEntity().getWorld().strikeLightningEffect(zombie.getBukkitEntity().getLocation());
                    zombie.getBukkitEntity().remove();
                    AIThread.ENTITES.remove(attackZombie);
                }
            }.runTaskLater(KingdomDefense.getInstance(), 20L * 30);
        }
    }

}

