package com.tadahtech.fadecloud.kd.menu.menus;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.HeadItems;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Created by Timothy Andis (TadahTech) on 8/1/2015.
 */
public class StatMenu extends Menu {

    private PlayerInfo info;

    public StatMenu(PlayerInfo info) {
        super(ChatColor.DARK_PURPLE + "Statistics");
        this.info = info;
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[45];
        buttons = pane(buttons);
        double kd = info.getKills() / (info.getDeaths() == 0 ? 1 : info.getDeaths());
        ItemStack kills_deaths = new ItemBuilder(new ItemStack(Material.DIAMOND_SWORD))
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "PVP Statistics")
          .lore(" ",
            ChatColor.GRAY + "Kills: " + ChatColor.AQUA + info.getKills(),
            ChatColor.GRAY + "Deaths: " + ChatColor.AQUA + info.getDeaths(),
            ChatColor.GRAY + "K/D Ratio: " + ChatColor.AQUA + kd)
          .build();
        ItemStack creeper = new ItemBuilder(new ItemStack(Material.SKULL_ITEM))
          .data((byte) 4)
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Creeper Statistics")
          .lore(" ",
            ChatColor.GRAY + "Wins: " + ChatColor.AQUA + info.getWins(TeamType.CREEPER),
            ChatColor.GRAY + "Level: " + ChatColor.AQUA + info.getLevel(TeamType.CREEPER))
          .build();
        ItemStack skeleton = new ItemBuilder(HeadItems.SKELETON)
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Skeleton Statistics")
          .lore(" ",
            ChatColor.GRAY + "Wins: " + ChatColor.AQUA + info.getWins(TeamType.SKELETON),
            ChatColor.GRAY + "Level: " + ChatColor.AQUA + info.getLevel(TeamType.SKELETON))
          .build();
        ItemStack zombie = new ItemBuilder(new ItemStack(Material.SKULL_ITEM))
          .data((byte) 2)
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Zombie Statistics")
          .lore(" ",
            ChatColor.GRAY + "Wins: " + ChatColor.AQUA + info.getWins(TeamType.ZOMBIE),
            ChatColor.GRAY + "Level: " + ChatColor.AQUA + info.getLevel(TeamType.ZOMBIE))
          .cloneBuild();
        ItemStack enderman = HeadItems.ENDERMAN.clone();
        ItemMeta meta = enderman.getItemMeta();
          meta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Enderman Statistics");
          meta.setLore(Arrays.asList(" ",
            ChatColor.GRAY + "Wins: " + ChatColor.AQUA + info.getWins(TeamType.ENDERMAN),
            ChatColor.GRAY + "Level: " + ChatColor.AQUA + info.getLevel(TeamType.ENDERMAN)));
        enderman.setItemMeta(meta);
        buttons[13] = new ShowButton(kills_deaths);
        buttons[21] = new ShowButton(creeper);
        buttons[23] = new ShowButton(zombie);
        buttons[30] = new ShowButton(skeleton);
        buttons[32] = new ShowButton(enderman);
        return buttons;
    }

    @Override
    public void onClose(Player player) {
        remove(player.getUniqueId());
    }

    private class ShowButton extends Button {

        private ShowButton(ItemStack item) {
            super(item, player -> {});
        }
    }
}
