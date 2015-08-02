package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.creation.GameMapCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis (TadahTech) on 7/30/2015.
 */
public class CreateCommand implements SubCommand {

    @Override
    public String getName() {
        return "create";
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
        return "Create an arena";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new GameMapCreator((Player) sender);
    }
}
