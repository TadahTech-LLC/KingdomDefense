package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class ChatCommand implements SubCommand {

    @Override
    public String getName() {
        return "togglechat";
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
        return "Toggle your chat between Team, and Global";
    }

    @Override
    public String[] getAliases() {
        return new String[] {"tc", "togglec","chat"};
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        boolean team = info.isTeamChat();
        String message = ChatColor.GRAY + "Chat set to " + ChatColor.RED + ChatColor.BOLD;
        if(team) {
            message += "GLOBAL";
        } else {
            message = "TEAM";
        }
        player.sendMessage(message);
        info.setTeamChat(!team);
    }
}
