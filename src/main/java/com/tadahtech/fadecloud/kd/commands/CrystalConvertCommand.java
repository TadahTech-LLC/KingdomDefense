package com.tadahtech.fadecloud.kd.commands;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.teg.fadecrystals.FadeCrystals;
import pw.teg.fadecrystals.FadePlayer;

/**
 * Created by Timothy Andis
 */
public class CrystalConvertCommand implements SubCommand {

    @Override
    public String getName() {
        return "convert";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Convert Crystals to Coins! 5 Crystals = 1 Coin.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        FadePlayer fadePlayer = FadeCrystals.get().getPlayer(player);
        double coins = info.getCoins();
        double crystals = fadePlayer.getCrystals();
        int amountTo;
        try {
            amountTo = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Please input a proper number!");
            return;
        }
        if(amountTo <= 0) {
            player.sendMessage(ChatColor.RED + "Please enter a value greater than 0!");
            return;
        }
        double needed = amountTo * 5;
        if(crystals < needed) {
            player.sendMessage(ChatColor.RED + "You don't have enough Crystals to do this!");
            return;
        }
        fadePlayer.setCrystals((long) (crystals - amountTo));
        info.setCoins(coins + amountTo);
    }
}
