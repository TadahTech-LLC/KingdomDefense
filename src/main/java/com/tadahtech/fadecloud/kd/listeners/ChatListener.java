package com.tadahtech.fadecloud.kd.listeners;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Timothy Andis
 */
public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String format = event.getFormat();
        Player player = event.getPlayer();
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        String wins = "" + (info.getWins(TeamType.CREEPER)  + info.getWins(TeamType.ZOMBIE)
          + info.getWins(TeamType.SKELETON) + info.getWins(TeamType.ENDERMAN));
        String teamName = info.getCurrentTeam() == null ? "" : info.getCurrentTeam().getType().fancy();
        format = format.replace("{WINS}", wins).replace("{TEAM_NAME}", teamName);
        event.setFormat(format);
    }
}
