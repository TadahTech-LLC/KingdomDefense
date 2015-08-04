package com.tadahtech.fadecloud.kd.menu.menus;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class PlayerMenu extends Menu {

    private PlayerInfo info;

    public PlayerMenu(PlayerInfo info) {
        super(ChatColor.DARK_PURPLE + "Profile");
        this.info = info;
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[27];
        buttons = pane(buttons, DyeColor.RED);
        ItemStack stats = new ItemBuilder(new ItemStack(Material.BOOK_AND_QUILL))
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Statistics")
          .lore(ChatColor.GRAY + "Click to view your Statistics page")
          .build();
        ItemStack levelUp = new ItemBuilder(new ItemStack(Material.DIAMOND_BLOCK))
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Level Up")
          .lore(ChatColor.GRAY + "Click to view level ups")
          .build();
        buttons[12] = new Button(stats, player -> new StatMenu(info).open(player));
        buttons[14] = new Button(levelUp, player -> new LevelUpMenu(info).open(player));
        return buttons;
    }

    @Override
    public void onClose(Player player) {

    }
}
