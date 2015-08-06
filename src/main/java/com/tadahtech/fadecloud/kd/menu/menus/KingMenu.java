package com.tadahtech.fadecloud.kd.menu.menus;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.items.ItemBuilder;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import com.tadahtech.fadecloud.kd.nms.King;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis
 */
public class KingMenu extends Menu {

    private PlayerInfo info;
    private King king;

    public KingMenu(PlayerInfo info) {
        super(ChatColor.DARK_PURPLE + info.getCurrentTeam().getType().getName() + " King");
        this.info = info;
        this.king = KingdomDefense.getInstance().getGame().getKing(info.getCurrentTeam().getType());
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[45];
        ItemStack structures = new ItemBuilder(new ItemStack(Material.ANVIL))
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Purchase Structures")
          .build();
        ItemStack kits = new ItemBuilder(new ItemStack(Material.GOLD_SWORD))
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "View Kits")
          .build();
        ItemStack king = new ItemBuilder(new ItemStack(Material.EMERALD))
          .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "King Health")
          .lore(" ", ChatColor.GREEN.toString() + this.king.getHealth())
          .build();
        buttons = pane(buttons, DyeColor.BLACK);
        buttons[20] = new Button(structures, player -> new PurchaseStructureMenu(info).open(player));
        buttons[24] = new Button(kits, player -> new KitMenu(info).open(player));
        buttons[22] = new Button(king, () ->{});
        return buttons;
    }

    @Override
    public void onClose(Player player) {

    }
}
