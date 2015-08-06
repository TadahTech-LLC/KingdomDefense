package com.tadahtech.fadecloud.kd.menu.menus;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.map.StructureType;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.map.structures.strucs.Archer;
import com.tadahtech.fadecloud.kd.map.structures.strucs.BlazeTower;
import com.tadahtech.fadecloud.kd.map.structures.strucs.Guardian;
import com.tadahtech.fadecloud.kd.map.structures.strucs.TeslaTower;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis (TadahTech) on 7/31/2015.
 */
public class PurchaseStructureMenu extends Menu {

    private PlayerInfo info;

    public PurchaseStructureMenu(PlayerInfo info) {
        super(ChatColor.DARK_PURPLE + "Purchase Structures");
        this.info = info;
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[27];
        buttons = pane(buttons, DyeColor.BLACK);
        ItemStack guardian = new ItemStack(Material.EMERALD_BLOCK);
        guardian = new ItemBuilder(guardian)
          .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Guardian")
          .lore(Structure.GUARDIAN_DESC)
          .build();
        ItemStack archer = new ItemStack(Material.SMOOTH_BRICK);
        archer = new ItemBuilder(archer)
          .name(ChatColor.GRAY.toString() + ChatColor.BOLD + "Archer")
          .lore(Structure.ARCHER_DESC)
          .build();

        ItemStack tesla = new ItemStack(Material.BEACON);
        tesla = new ItemBuilder(tesla)
          .name(ChatColor.AQUA.toString() + ChatColor.BOLD + "Tesla")
          .lore(Structure.TESLA_DESC)
          .build();
        ItemStack blaze = new ItemStack(Material.FIREBALL);
        blaze = new ItemBuilder(blaze)
          .name(ChatColor.RED.toString() + ChatColor.BOLD + "Blaze")
          .lore(Structure.BLAZE_DESC)
          .build();
        Island island = info.getCurrentTeam().getIsland();
        buttons[10] = new Button(guardian, player -> {
            int count = island.getCount(StructureType.GUARDIAN);
            if(count >= 4) {
                info.sendMessage(ChatColor.RED + "There are already 4 Guardian towers!");
                player.closeInventory();
                return;
            }
            if(!charge(StructureType.GUARDIAN, info)) {
                player.closeInventory();
                info.sendMessage(ChatColor.RED + "You cannot afford this!");
                return;
            }
            info.remove(StructureType.GUARDIAN.getCost());
            new Guardian(info).give(player);
            player.closeInventory();
        });
        buttons[12] = new Button(archer, player -> {
            int count = island.getCount(StructureType.ARCHER);
            if(count >= 4) {
                info.sendMessage(ChatColor.RED + "There are already 4 Archer towers!");
                player.closeInventory();
                return;
            }
            if(!charge(StructureType.ARCHER, info)) {
                player.closeInventory();
                info.sendMessage(ChatColor.RED + "You cannot afford this!");
                return;
            }
            info.remove(StructureType.ARCHER.getCost());
            new Archer(info).give(player);
            player.closeInventory();
        });
        buttons[14] = new Button(blaze, player -> {
            int count = island.getCount(StructureType.BLAZE);
            if(count >= 4) {
                info.sendMessage(ChatColor.RED + "There are already 4 Blaze towers!");
                player.closeInventory();
                return;
            }
            if(!charge(StructureType.BLAZE, info)) {
                player.closeInventory();
                info.sendMessage(ChatColor.RED + "You cannot afford this!");
                return;
            }
            new BlazeTower(info).give(player);
            info.remove(StructureType.BLAZE.getCost());
            player.closeInventory();
        });
        buttons[16] = new Button(tesla, player ->{
            int count = island.getCount(StructureType.TESLA);
            if(count >= 4) {
                info.sendMessage(ChatColor.RED + "There are already 4 Tesla towers!");
                player.closeInventory();
                return;
            }
            if(!charge(StructureType.TESLA, info)) {
                player.closeInventory();
                info.sendMessage(ChatColor.RED + "You cannot afford this!");
                return;
            }
            info.remove(StructureType.TESLA.getCost());
            new TeslaTower(info).give(player);
            player.closeInventory();
        });
        return buttons;
    }

    @Override
    public void onClose(Player player) {

    }

    public boolean charge(StructureType type, PlayerInfo info) {
        return info.hasEnough(type.getCost());
    }
}
