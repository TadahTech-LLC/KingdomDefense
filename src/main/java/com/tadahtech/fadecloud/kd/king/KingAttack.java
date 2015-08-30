package com.tadahtech.fadecloud.kd.king;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Random;

/**
 * Created by Timothy Andis
 */
public interface KingAttack {

    void attack(Player player, King king);

    String getName();

    double getChance();

    default boolean should() {
        if(getHits() >= hitsTillAttack()) {
            resetHits();
            return true;
        }
        Random random = new Random();
        return random.nextInt(100) <= getChance();
    }

    void resetHits();

    int hitsTillAttack();

    void hit();

    int getHits();

    default void message(Player player, King king) {
        player.sendMessage(king.getTeam().getType().fancy() + " " + ChatColor.AQUA + " used " + getName());
    }


}
