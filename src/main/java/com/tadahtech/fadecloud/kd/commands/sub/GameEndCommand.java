package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.lang.Lang;
import org.bukkit.command.CommandSender;

/**
 * Created by Timothy Andis
 */
public class GameEndCommand implements SubCommand {
    @Override
    public String getName() {
        return "end";
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
        return "Force end a game";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        KingdomDefense.getInstance().getGame().end();
        sender.sendMessage(Lang.PREFIX + "Force ended the game");
    }
}
