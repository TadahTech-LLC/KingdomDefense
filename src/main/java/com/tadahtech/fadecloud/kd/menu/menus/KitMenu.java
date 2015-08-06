package com.tadahtech.fadecloud.kd.menu.menus;

import com.tadahtech.fadecloud.kd.kit.CSKit;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.kit.kits.ThorKit;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

/**
 * Created by Timothy Andis
 */
public class KitMenu extends Menu {

    private PlayerInfo info;
    private ThorKit thorKit;

    public KitMenu(PlayerInfo info) {
        super(ChatColor.DARK_PURPLE + "Kits");
        this.info = info;
        if(thorKit == null) {
            this.thorKit = new ThorKit();
        }
    }

    @Override
    protected Button[] setUp() {
        CSKit[] kits = CSKit.getAll().toArray(new CSKit[CSKit.getAll().size()]);
        Button[] buttons = new Button[((kits.length + 8) / 9 * 9)];
        for(int i = 0; i < kits.length; i++) {
            CSKit kit = kits[i];
            if(kit.getName().equalsIgnoreCase("example")) {
                continue;
            }
            ItemStack itemStack = new ItemStack(kit.getItems().get(0));

            ItemMeta meta = itemStack.getItemMeta();

            if(!kit.hasPermission(info.getBukkitPlayer())) {
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

    }
}
