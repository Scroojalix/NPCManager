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

    public ClearCommand() {
        super(
            "clear",
            "Removes all NPC's on the server.",
            "/npc clear confirm",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Are you sure you wish to do this? Type "
                + ChatColor.YELLOW + "/npc clear confirm");
            return true;
        }
        if (!PluginUtils.noNPCs() && args[1].equalsIgnoreCase("confirm")) {
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
