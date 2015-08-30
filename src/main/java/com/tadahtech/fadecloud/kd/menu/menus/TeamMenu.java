package com.tadahtech.fadecloud.kd.menu.menus;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class TeamMenu extends Menu {

    private CSTeam[] teams;

    public TeamMenu(CSTeam[] teams) {
        super(ChatColor.DARK_PURPLE + "Choose Team");
        this.teams = teams;
    }

    @Override
    protected Button[] setUp() {
        Button[] buttons = new Button[9];
        int slot = 0;
        for(int i = 0; i < teams.length; i++) {
            CSTeam team = teams[i];
            ItemStack itemStack = team.getMenuIcon();
            if(itemStack.getAmount() == 0) {
                itemStack.setAmount(1);
            }
            buttons[slot] = new Button(itemStack, player -> {
                if(team.getSize() >= 5) {
                    player.sendMessage(ChatColor.RED + "This team has too many players already! Pick another!");
                    return;
                }
                player.sendMessage(ChatColor.GREEN + "You are on the " + team.getType().fancy() + " team.");
                KingdomDefense.getInstance().getInfoManager().get(player).setCurrentTeam(team);
                player.closeInventory();
            });
            slot += 2;
        }
        return buttons;
    }

    @Override
    public void onClose(Player player) {
        remove(player.getUniqueId());
    }
}
