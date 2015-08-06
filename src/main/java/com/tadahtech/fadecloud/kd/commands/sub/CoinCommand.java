package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class CoinCommand implements SubCommand {

    @Override
    public String getName() {
        return "addcoins";
    }

    @Override
    public String getPermission() {
        return "kd.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Add coins to a player";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String target = args[0];
        int amount = Integer.parseInt(args[1]);
        Player player = Bukkit.getPlayer(target);
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        info.setCoins(info.getCoins() + amount);
        sender.sendMessage(ChatColor.GREEN + target + "'s is now " + amount + " richer!");
    }
}
