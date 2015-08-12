package com.tadahtech.fadecloud.kd.csc.packets.response;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.tadahtech.fadecloud.kd.KingdomDefense;
import com.tadahtech.fadecloud.kd.csc.packets.ResponsePacket;
import com.tadahtech.fadecloud.kd.game.Game;
import com.tadahtech.fadecloud.kd.game.GameState;
import com.tadahtech.fadecloud.kd.info.PlayerInfo;
import com.tadahtech.fadecloud.kd.lang.Lang;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Timothy Andis (TadahTech) on 7/27/2015.
 */
public class JoinGameResponsePacket extends ResponsePacket {

    private String uuid, from;

    public JoinGameResponsePacket() {

    }

    public JoinGameResponsePacket(String uuid, String from) {
        this.uuid = uuid;
        this.from = from;
    }

    @Override
    public void write() {
        Game game = KingdomDefense.getInstance().getGame();
        if(game == null) {
            this.send("fail=noGame:" + uuid + ":" + from);
            return;
        }
        GameState state = game.getState();
        if(state == GameState.WAITING || state == GameState.COUNTDOWN) {
            if(game.getPlayers().size() >= game.getMap().getMax()) {
                this.send("fail=full" + uuid + ":" + from);
            }
            this.send("yes:" + uuid + ":" + from);
            return;
        }
        this.send("yes=inProgress:" + uuid + ":" + from);
    }

    @Override
    public void handle(String message) {
        String[] uuidArray = message.split(":");
        String[] response = uuidArray[0].split("=");
        Player player = Bukkit.getPlayer(UUID.fromString(uuidArray[1]));
        if(player == null) {
            return;
        }
        PlayerInfo info = KingdomDefense.getInstance().getInfoManager().get(player);
        if(uuidArray[0].equalsIgnoreCase("yes")) {
            String from = uuidArray[2];
            if(message.contains("=")) {
                Lang.GAME_FOUND_TO_SPECTATE.send(info, ImmutableMap.of("server", from));
                //in process;
                return;
            } else {
                Lang.GAME_FOUND.send(info, ImmutableBiMap.of("server", from));
            }
            KingdomDefense.getInstance().redirect(from, player);
            return;
        }
        String reason = response[1];
        String sent;
        switch (reason.toLowerCase()) {
            case "noGame":
                sent = "This server is currently being worked on! It'll be back soon!";
                break;
            case "full":
                sent = "This server is full! Try another!";
                break;
            default:
                return;
        }
        player.sendMessage(ChatColor.RED + sent);
    }
}
