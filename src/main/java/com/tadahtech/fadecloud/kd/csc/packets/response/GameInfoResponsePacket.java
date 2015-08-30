package com.tadahtech.fadecloud.kd.csc.packets.response;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.ResponsePacket;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.sign.HeartbeatThread;
import com.tadahtech.fadecloud.kd.sign.LobbySign;
import org.bukkit.Bukkit;

import java.util.Optional;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class GameInfoResponsePacket extends ResponsePacket {

    public int players, max;
    public GameState state;
    public String arena;

    @Override
    public void write() {
        Game game = KingdomDefense.getInstance().getGame();
        if (game == null) {
            this.send("noGame:" + KingdomDefense.getInstance().getUIName());
            return;
        }
        //noinspection StringBufferReplaceableByString
        StringBuilder builder = new StringBuilder();
        builder.append(KingdomDefense.getInstance().getUIName())
          .append(":")
          .append(game.getState().toString())
          .append(":")
          .append(Bukkit.getOnlinePlayers().size())
          .append(":")
          .append(game.getMap().getMax())
          .append(":");
        this.send(builder.toString());
    }

    @Override
    public void handle(String message) {
        String[] str = message.split(":");
        if(str[0].equalsIgnoreCase("noGame")) {
            this.arena = str[1];
            this.state = GameState.DOWN;
            this.players = -1;
            this.max = -1;
            return;
        }
        this.arena = str[0];
        this.state = GameState.fromString(str[1]);
        this.players = Integer.parseInt(str[2]);
        this.max = Integer.parseInt(str[3]);
        Optional<LobbySign> maybeSign = LobbySign.get(arena);
        HeartbeatThread.respond();
        if(!maybeSign.isPresent()) {
            new LobbySign(arena);
        } else {
            maybeSign.get().update(this);
        }

    }
}
