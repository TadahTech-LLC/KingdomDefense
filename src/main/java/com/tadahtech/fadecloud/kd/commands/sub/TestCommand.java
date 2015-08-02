package com.tadahtech.fadecloud.kd.commands.sub;

import com.tadahtech.fadecloud.kd.commands.SubCommand;
import com.tadahtech.fadecloud.kd.menu.menus.TeamMenu;
import com.tadahtech.fadecloud.kd.teams.CSTeam;
import com.tadahtech.fadecloud.kd.teams.creeper.CreeperTeam;
import com.tadahtech.fadecloud.kd.teams.enderman.EndermanTeam;
import com.tadahtech.fadecloud.kd.teams.skeleton.SkeletonTeam;
import com.tadahtech.fadecloud.kd.teams.zombie.ZombieTeam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Andis (TadahTech) on 7/30/2015.
 */
public class TestCommand implements SubCommand {

    @Override
    public String getName() {
        return "test";
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
        return "Testicles";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
//        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        new TeamMenu(new CSTeam[]{
          new CreeperTeam(null), new ZombieTeam(null), new EndermanTeam(null), new SkeletonTeam(null)})
          .open(player);
    }
}
