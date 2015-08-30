package com.tadahtech.fadecloud.kd.king;

import com.google.common.collect.Lists;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.teams.CSTeam.TeamType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by Timothy Andis
 */
public class KingSummonThread extends BukkitRunnable {

    private Game game;
    private List<King> kings;

    public KingSummonThread() {
        this.game = KingdomDefense.getInstance().getGame();
        this.kings = Lists.newArrayList();
        kings.add(game.getKing(TeamType.CREEPER));
        kings.add(game.getKing(TeamType.ZOMBIE));
        kings.add(game.getKing(TeamType.SKELETON));
        kings.add(game.getKing(TeamType.ENDERMAN));
    }

    @Override
    public void run() {
        if(game.getState() != GameState.BATTLE) {
            return;
        }
        kings.stream().forEach(king -> {
        });
    }
}
