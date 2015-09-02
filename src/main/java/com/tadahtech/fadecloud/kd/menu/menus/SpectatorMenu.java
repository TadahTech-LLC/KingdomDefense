package com.tadahtech.fadecloud.kd.menu.menus;

import com.google.common.collect.ImmutableMap;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.items.HeadItems;
import com.tadahtech.fadecloud.kd.lang.Lang;
import com.tadahtech.fadecloud.kd.menu.Button;
import com.tadahtech.fadecloud.kd.menu.Menu;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Timothy Andis
 */
public class SpectatorMenu extends Menu {

    public SpectatorMenu() {
        super(ChatColor.DARK_PURPLE + "Spectate");
    }

    @Override
    protected Button[] setUp() {
        Game game = KingdomDefense.getInstance().getGame();
        List<Player> players = game.getBukkitPlayers();
        players = players.stream().filter(player -> !player.hasMetadata("spectator")).collect(Collectors.toList());
        Button[] buttons = new Button[45];
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            CSTeam team = KingdomDefense.getInstance().getInfoManager().get(player).getCurrentTeam();
            ItemStack itemStack = HeadItems.getItem(player.getName());
            ItemMeta builder = itemStack.getItemMeta();
            builder.setDisplayName(ChatColor.AQUA + player.getName());
            builder.setLore(Arrays.asList(ChatColor.GRAY + "Health: " + ChatColor.AQUA + Math.round(((CraftPlayer) player).getHealth()),
              ChatColor.GRAY + "Team: " + ChatColor.AQUA + (team == null ? "None" : team.getType().fancy())));
            itemStack.setItemMeta(builder);
            buttons[i] = new Button(itemStack, player1 -> {
                player1.teleport(player);
                Lang.SPECTATING_PLAYER.send(player1, ImmutableMap.of("other", player.getName()));
                player1.closeInventory();
                player1.playSound(player1.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
            });
        }
        return buttons;
    }

    @Override
    public void onClose(Player player) {
        remove(player.getUniqueId());
    }
}
