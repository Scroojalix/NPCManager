package io.github.scroojalix.npcmanager.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.subcommands.ClearCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.CreateCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.InfoCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.ListCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.ModifyCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.MoveCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.ReloadCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.RemoveCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.TeleportCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class CommandManager implements TabExecutor {

    private final NPCMain main;
    private ArrayList<SubCommand> subcommands = new ArrayList<SubCommand>();

    public CommandManager(NPCMain main) {
        this.main = main;
        subcommands.add(new CreateCommand());
		subcommands.add(new ModifyCommand());
        subcommands.add(new RemoveCommand());
        subcommands.add(new MoveCommand());
        subcommands.add(new TeleportCommand());
        subcommands.add(new ListCommand());
        subcommands.add(new ClearCommand());
        subcommands.add(new InfoCommand());
        subcommands.add(new ReloadCommand());
        //TODO add rename command

        main.getCommand("npc").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand command : subcommands) {
                if (args[0].equalsIgnoreCase(command.getName())) {
                    if (command.consoleCanRun() || sender instanceof Player) {
                        if (!command.execute(main, sender, args)) {
                            sender.sendMessage(ChatColor.RED + "Usage: " + command.getSyntax());
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Sorry console, but you can't do that.");
                    }
                    return true;
                }
            }
            return false;
        }
        for (SubCommand sub : subcommands) {
            sender.sendMessage(PluginUtils.format("&B" + sub.getSyntax() + " &F&L-&6 " + sub.getDescription()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> result = new ArrayList<String>();
		if (args.length == 1) {
			for (SubCommand sub : subcommands) {
				if (sub.getName().startsWith(args[0])) {
					result.add(sub.getName());
				}
			}
		} else {
			for (SubCommand sub : subcommands) {
				if (args[0].equalsIgnoreCase(sub.getName())) {
					result = sub.onTabComplete(args);
				}
			}
		}
		return result;
    }
}
