package com.tadahtech.fadecloud.kd.teams.zombie;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.nms.mobs.AttackZombie;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
import com.tadahtech.fadecloud.kd.utils.PacketUtil;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R2.EntityZombie;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Timothy Andis
 */
public class ZombieItem extends ModSpecialItem {

    public ZombieItem() {
        super(ItemBuilder.wrap(new ItemStack(Material.SKULL_ITEM, 1, (byte) 2))
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

                private int reps = 0, threeReps = 0, timeLeft = 30; ;
                private String green = ChatColor.GREEN + "█";
                private String red = ChatColor.RED + "█";

                @Override
                public void run() {
                    int left = 0;
                    StringBuilder builder = new StringBuilder();
                    builder.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Time Remaining: ");
                    for (int i = 10; i > threeReps; i--) {
                        builder.append(green);
                        left++;
                    }
                    for (int i = 0; i < left; ) {
                        builder.append(red);
                    }
                    builder.append(ChatColor.GRAY).append(" (");
                    builder.append(timeLeft).append("s)");
                    PacketUtil.sendActionBarMessage(player, builder.toString());
                    reps++;
                    timeLeft--;
                    if (reps % 3 == 0) {
                        threeReps++;
                    }
                    if (reps >= 10) {
                        cancel();
                        zombie.getBukkitEntity().getWorld().strikeLightningEffect(zombie.getBukkitEntity().getLocation());
                        zombie.getBukkitEntity().remove();
                    }
                }
            }.runTaskTimer(KingdomDefense.getInstance(), 0L, 20L);
        }
    }

    @Override
    protected long getCooldown() {
        return 120;
    }
}

