package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.teams.ModSpecialItem;
import org.bukkit.command.CommandSender;

/**
 * Created by Timothy Andis
 */
public class ForceStartCommand implements SubCommand {

    @Override
    public String getName() {
        return "force";
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
        return "Force start a game";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        KingdomDefense.getInstance().getGame().start();
        sender.sendMessage(ModSpecialItem.PREFIX + "Force started the game");
    }
}
