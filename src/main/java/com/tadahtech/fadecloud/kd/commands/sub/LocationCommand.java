package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.map.Bridge;
import com.tadahtech.fadecloud.kd.map.GameMap;
import com.tadahtech.fadecloud.kd.map.Island;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis
 */
public class LocationCommand implements SubCommand {

    private Location min, max;

    @Override
    public String getName() {
        return "setlocation";
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
        return "set a location type.";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String type = args[0];
        GameMap map = KingdomDefense.getInstance().getMap();
        Bridge bridge = map.getBridge();
        if (type.equalsIgnoreCase("bridgemin")) {
            this.min = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "Min point set");
            return;
        } else if (type.equalsIgnoreCase("bridgemax")) {
            this.max = player.getLocation();
            player.sendMessage(ChatColor.GREEN + "Max point set");
            return;
        } else if (type.equalsIgnoreCase("setBridge")) {
            map.setBridge(null);
            if(max == null) {
                max = bridge.getMax();
            }
            if(min == null) {
                min = bridge.getMin();
            }
            bridge = new Bridge(min, max);
            map.setBridge(bridge);
            player.sendMessage(ChatColor.GREEN + "Redid the bridge.");
            KingdomDefense.getInstance().getMapIO().save();
            return;
        }
        TeamType teamType = TeamType.valueOf(type.toUpperCase());
        Island island = map.getIslands().get(teamType);
        String loc = args[1];
        if (loc.equalsIgnoreCase("castleMin")) {
            island.getCastleRegion().setMin(player.getLocation());
        } else if (loc.equalsIgnoreCase("castleMax")) {
            island.getCastleRegion().setMax(player.getLocation());
        } else if (loc.equalsIgnoreCase("save")) {
            KingdomDefense.getInstance().getMapIO().save();
        }
    }
}
