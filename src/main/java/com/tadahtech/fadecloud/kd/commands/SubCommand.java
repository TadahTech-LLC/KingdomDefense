package com.tadahtech.fadecloud.kd.commands;

import org.bukkit.command.CommandSender;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public interface SubCommand {

    String getName();

    String getPermission();

    boolean isPlayerOnly();

    String getDescription();

    String[] getAliases();

    void execute(CommandSender sender, String[] args);

    default boolean isInternal() {
        return false;
    }

}
