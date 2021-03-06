package com.tadahtech.fadecloud.kd.menu.menus;

import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class KitMenu extends Menu {

    private PlayerInfo info;

    public KitMenu(PlayerInfo info) {
        super(ChatColor.DARK_PURPLE + "Kits");
        this.info = info;
    }

    @Override
    protected Button[] setUp() {
        List<CSKit> csKits = CSKit.getAll()
          .stream()
          .filter(kit -> !kit.getName().equalsIgnoreCase("example"))
          .collect(Collectors.toList());
        CSKit[] kits = csKits.toArray(new CSKit[csKits.size()]);
        Button[] buttons = new Button[((kits.length + 8) / 9 * 9)];
        for (int i = 0; i < kits.length; i++) {
            CSKit kit = kits[i];
            ItemStack itemStack = kit.getItems().get(0);
            itemStack = itemStack.clone();

            ItemMeta meta = itemStack.getItemMeta();

            if (!kit.hasPermission(info.getBukkitPlayer())) {
                itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getWoolData());
                meta = itemStack.getItemMeta();
                meta.setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + kit.getName());
                meta.setLore(Collections.singletonList(ChatColor.RED + "Unlock this Kit in the Store!"));
            } else {
                meta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + kit.getName());
                meta.setLore(kit.toLore());
            }

            itemStack.setItemMeta(meta);
            buttons[i] = new Button(itemStack, kit::give);
        }
        return buttons;
    }

    @Override
    public void onClose(Player player) {
        remove(player.getUniqueId());
    }
}
