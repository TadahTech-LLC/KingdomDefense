package com.tadahtech.fadecloud.kd.game;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import net.md_5.bungee.api.ChatColor;

/**
 * @author Tim [calebbfmv]
 */
public enum GameState {

    WAITING,
    COUNTDOWN,
    PEACE,
    BATTLE,
    PINGING,
    DOWN;

    @Override
    public String toString() {
        switch (this) {
            case COUNTDOWN:
                return "Countdown";
            case PEACE:
                return "Peace";
            case BATTLE:
                return "Battle";
            case WAITING:
                return "Waiting";
            case PINGING:
                return "Pinging";
            case DOWN:
                return "Offline";
        }
        return null;
    }

    public String format() {
        switch (this) {
            case COUNTDOWN: return ChatColor.DARK_PURPLE + "Countdown";
            case PEACE: return ChatColor.GOLD + "Peace";
            case BATTLE: return ChatColor.RED + "Battle";
            case WAITING: {
                if(KingdomDefense.getInstance().getGame() != null) {
                    return ChatColor.GREEN.toString() + "Waiting";
                }
                return ChatColor.GREEN + "Click to join";
            }
            case PINGING: return ChatColor.GRAY + "Pinging....";
            case DOWN: return ChatColor.DARK_RED + "Offline";
        }
        return null;
    }

    public static GameState fromString(String s) {
        switch (s.toLowerCase()) {
            case "waiting": return WAITING;
            case "peace": return PEACE;
            case "battle": return BATTLE;
            case "countdown": return COUNTDOWN;
            case "pinging": return PINGING;
            case "down": return DOWN;
        }
        return null;
    }

}
