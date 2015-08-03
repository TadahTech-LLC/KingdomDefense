package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.commands.SubCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Timothy Andis
 */
public class EditModeCommand implements SubCommand {
    @Override
    public String getName() {
        return "tem";
    }

    @Override
    public String getPermission() {
        return "kd.editmode";
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
        return new String[] {
          "editMode",
          "toggleEditMode"
        };
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        KingdomDefense.EDIT_MODE = !KingdomDefense.EDIT_MODE;
        sender.sendMessage(ChatColor.GREEN + "Toggled Edit mode.");
    }
}
