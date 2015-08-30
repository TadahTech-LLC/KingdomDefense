package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.creation.StructureRegionCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class CreateRegionCommand implements SubCommand {

    @Override
    public String getName() {
        return "nsr";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new StructureRegionCreator((Player) sender);
    }
}
