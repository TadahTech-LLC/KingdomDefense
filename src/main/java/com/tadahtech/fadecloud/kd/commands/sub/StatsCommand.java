package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.menu.menus.StatMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class StatsCommand implements SubCommand {
    @Override
    public String getName() {
        return "stats";
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
        return "Show your stats menu!";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get((Player) sender);
        new StatMenu(info).open((Player) sender);
    }
}
