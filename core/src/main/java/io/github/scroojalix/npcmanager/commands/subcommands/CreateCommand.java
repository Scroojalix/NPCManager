package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.chat.Messages;

public class CreateCommand extends SubCommand {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates an NPC at your location.";
    }

    @Override
    public String getSyntax() {
        return "/npc create <name> [--doNotStore]";
    }

    @Override
    public boolean consoleCanRun() {
        return false;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 2)
            return false;

        String name = args[1];
        if (main.npc.getNPCs().containsKey(name)) {
            sender.sendMessage(ChatColor.RED+Messages.NPC_EXISTS);
            return true;
        } else if (name.length() > 16) {
            sender.sendMessage(ChatColor.RED+Messages.LONG_NAME);
            return true;
        } else if (!PluginUtils.isAlphanumeric(name)) {
            sender.sendMessage(ChatColor.RED+Messages.NOT_ALPHANUMERIC);
            return true;
        }
        boolean store = true;
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("--doNotStore")) {
                store = false;
            }
        }
        main.npc.createNPC(name, ((Player)sender).getLocation(), store);
        sender.sendMessage(PluginUtils.format("&6Created an NPC named &F")+name);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(String[] args) {
        ArrayList<String> result = new ArrayList<String>();
        if (args.length == 3) {
            result.add("--doNotStore");
        }
        return result;
    }
}
