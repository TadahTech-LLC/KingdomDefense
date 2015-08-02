package com.tadahtech.fadecloud.kd.game;

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
    ;

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
        }
        return null;
    }

    public String format() {
        switch (this) {
            case COUNTDOWN: return ChatColor.DARK_PURPLE + "Countdown";
            case PEACE: return ChatColor.GOLD + "Peace";
            case BATTLE: return ChatColor.RED + "Battle";
            case WAITING: return ChatColor.GREEN + "Click to join";
            case PINGING: return ChatColor.GRAY + "Pinging....";
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
        }
        return null;
    }

}
