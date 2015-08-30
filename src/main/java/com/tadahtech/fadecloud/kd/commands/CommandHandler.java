package com.tadahtech.fadecloud.kd.commands;

import com.tadahtech.fadecloud.kd.KingdomDefense;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timothy Andis (TadahTech) on 7/28/2015.
 */
public class CommandHandler implements CommandExecutor {

    private Map<String, SubCommand> subCommands = new HashMap<>();
    private Map<String, SubCommand> subCommandAliases = new HashMap<>();

    public CommandHandler() {
        KingdomDefense.getInstance().getCommand("kd").setExecutor(this);
    }

    public Collection<SubCommand> getSubCommands() {
        return subCommands.values();
    }

    public void register(SubCommand command) {
        String[] aliases = command.getAliases();
        String name = command.getName();
        subCommands.put(name.toLowerCase(), command);
        if (aliases != null && aliases.length > 0) {
            Arrays.asList(aliases).stream().forEach(s -> subCommandAliases.put(s.toLowerCase(), command));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args == null || args.length == 0) {
            return true;
        }
        String cmd = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(cmd);
        if (subCommand == null) {
            subCommand = subCommandAliases.get(cmd);
        }
        //No command, or alias registered.
        if (subCommand == null) {
            sender.sendMessage(ChatColor.RED + "Idk whut this is");
            sender.sendMessage(args[0]);
            return true;
        }
        String[] cmdArgs = new String[args.length];
        for (int i = 1; i < args.length; i++) {
            String s = args[i];
            cmdArgs[i - 1] = s;
        }
        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is player only!");
            return true;
        }
        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do this!");
            return true;
        }
        subCommand.execute(sender, cmdArgs);
        return true;
    }
}
