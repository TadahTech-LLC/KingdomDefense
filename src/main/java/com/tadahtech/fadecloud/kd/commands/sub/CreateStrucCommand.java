package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.map.StructureType;
import com.tadahtech.fadecloud.kd.map.structures.StructureRegion;
import com.tadahtech.fadecloud.kd.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class CreateStrucCommand implements SubCommand {

    @Override
    public String getName() {
        return "createStruc";
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
        return "Create a structure, please, don't do this command. I mean please, don't. I'll murder you if you try.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String[] locations = args[0].split(":");
        Location min = Utils.fromXYZ(locations[0], player);
        Location max = Utils.fromXYZ(locations[1], player);
        Location firing = Utils.fromXYZ(locations[2], player);
        StructureType type = StructureType.valueOf(args[1].toUpperCase());
        new StructureRegion(min, max, type, firing);
        player.sendMessage(ChatColor.AQUA + "Created a new " + type.name() + " structure region!");
    }
}
