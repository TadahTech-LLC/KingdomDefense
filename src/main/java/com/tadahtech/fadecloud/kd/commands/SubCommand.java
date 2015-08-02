package com.tadahtech.fadecloud.kd.commands;

import org.bukkit.command.CommandSender;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public interface SubCommand {

    public String getName();

    public String getPermission();

    public boolean isPlayerOnly();

    public String getDescription();

    public String[] getAliases();

    public void execute(CommandSender sender, String[] args);

}
