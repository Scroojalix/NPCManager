package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class ClearCommand extends SubCommand {

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Removes all NPC's on the server.";
    }

    @Override
    public String getSyntax() {
        return "/npc clear";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (!PluginUtils.noNPCs()) {
            main.npc.removeAllNPCs(true);
            sender.sendMessage(PluginUtils.format("&6Removed all NPC's."));
        } else {
            sender.sendMessage(ChatColor.RED+Messages.NO_NPCS);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        return new ArrayList<String>();
    }
    
}
