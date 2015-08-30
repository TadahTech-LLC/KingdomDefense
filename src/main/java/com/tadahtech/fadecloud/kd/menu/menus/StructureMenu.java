package com.tadahtech.fadecloud.kd.menu.menus;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.map.structures.Structure;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class StructureMenu extends Menu {

    private Structure structure;
    private double cost;

    public StructureMenu(Structure structure) {
        super(ChatColor.DARK_PURPLE + "Activate " + ChatColor.BLACK + structure.getBaseName());
        this.structure = structure;
        this.cost = structure.getStructureType().getCost();
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[27];
        buttons = pane(buttons);
        ItemStack activate = new ItemBuilder(new ItemStack(Material.EMERALD))
          .name(ChatColor.AQUA.toString() + ChatColor.BOLD + "Activate")
          .lore(ChatColor.GRAY + "Cost: " + ChatColor.AQUA + cost)
          .build();
        buttons[13] = new Button(activate, player -> {
            PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
            if (!info.hasEnough(cost)) {
                player.closeInventory();
                info.sendMessage(ChatColor.RED + "You cannot afford this!");
                return;
            }
            info.remove(cost);
            structure.activate(info);
            player.closeInventory();
        });
        return buttons;
    }

    @Override
    public void onClose(Player player) {
        remove(player.getUniqueId());
    }
}
