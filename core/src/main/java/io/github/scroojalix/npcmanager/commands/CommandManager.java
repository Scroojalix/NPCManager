package io.github.scroojalix.npcmanager.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.subcommands.*;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class CommandManager implements TabExecutor {

    private final NPCMain main;
    private ArrayList<SubCommand> subcommands = new ArrayList<SubCommand>();

    public CommandManager(NPCMain main) {
        this.main = main;
        subcommands.add(new CreateCommand());
		subcommands.add(new ModifyCommand());
		subcommands.add(new InteractionCommand());
		subcommands.add(new EquipmentCommand());
        subcommands.add(new RemoveCommand());
        subcommands.add(new MoveCommand());
        subcommands.add(new TeleportCommand());
        subcommands.add(new ListCommand());
        subcommands.add(new ClearCommand());
        subcommands.add(new InfoCommand());
        subcommands.add(new ReloadCommand());
        main.getCommand("npc").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < subcommands.size(); i++) {
                if (args[0].equalsIgnoreCase(subcommands.get(i).getName())) {
                    SubCommand command = subcommands.get(i);
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

    private List<String> arg0 = new ArrayList<String>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (arg0.isEmpty()) {
            for (SubCommand sub : subcommands) {
                arg0.add(sub.getName());
            }
        }
        List<String> result = new ArrayList<String>();
		switch (args.length) {
		case 1:
			for (String a : arg0) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(a);
				}
			}
			return result;
		case 2:
			result.clear();
			if (args[0].equalsIgnoreCase("remove")
				|| args[0].equalsIgnoreCase("modify")
				|| args[0].equalsIgnoreCase("interactEvent")
				|| args[0].equalsIgnoreCase("move")
				|| args[0].equalsIgnoreCase("info")
				|| args[0].equalsIgnoreCase("tpto")
				|| args[0].equalsIgnoreCase("equipment")) {
				for (String npc : main.npc.getNPCs().keySet()) {
					if (npc.toLowerCase().startsWith(args[1].toLowerCase())) result.add(npc);
				}
				return result;
			}
			break;
		case 3:
			result.clear();
			if (args[0].equalsIgnoreCase("modify")) {
				List<String> arg2 = new ArrayList<String>();
				arg2.add("displayName"); arg2.add("subtitle");
				arg2.add("hasHeadRotation"); arg2.add("range");
				arg2.add("skin");
				for (String a : arg2) {
					if (a.toLowerCase().startsWith(args[2].toLowerCase())) result.add(a);
				}
				return result;
			} else if (args[0].equalsIgnoreCase("interactEvent")) {
				List<String> arg2 = new ArrayList<String>();
				arg2.add("command"); arg2.add("custom");
				arg2.add("none");
				for (String a : arg2) {
					if (a.toLowerCase().startsWith(args[2].toLowerCase())) result.add(a);
				}
				return result;
			}
			break;
		case 4:
			result.clear();
			if (args[0].equalsIgnoreCase("modify")) {
				List<String> arg3 = new ArrayList<String>();
				if (args[2].equalsIgnoreCase("displayName") || args[2].equalsIgnoreCase("subtitle")) {
					arg3.add("null");
					for (String a : arg3) {
						if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
					}
					return result;
				}
				else if (args[2].equalsIgnoreCase("hasHeadRotation")) {
					arg3.add("true"); arg3.add("false");
					for (String a : arg3) {
						if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
					}
					return result;
				}
				else if (args[2].equalsIgnoreCase("skin")) {
					for (String skin : main.skinManager.values()) {
						arg3.add(skin);
					}
					arg3.add("Default");
					for (String a : arg3) {
						if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
					}
					return result;
				}
			} else if (args[0].equalsIgnoreCase("interactEvent") && args[2].equalsIgnoreCase("custom")) {
				List<String> arg3 = new ArrayList<String>();
				for (String interaction : InteractionsManager.getInteractEvents().keySet()) {
					arg3.add(interaction);
				}
				for (String a : arg3) {
					if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
				}
				return result;
			}
			break;
		}
        return new ArrayList<String>();
    }
}
