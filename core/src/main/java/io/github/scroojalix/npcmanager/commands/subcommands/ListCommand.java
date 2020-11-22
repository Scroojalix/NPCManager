package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class ListCommand extends SubCommand {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lists all NPC's on the server.";
    }

    @Override
    public String getSyntax() {
        return "/npc list";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (!main.npc.getNPCs().isEmpty()) {
            sender.sendMessage(PluginUtils.format("&6List of all NPC's &7&o(Click to Remove)"));
            for (String npc : main.npc.getNPCs().keySet()) {
                CommandUtils.sendJSONMessage(sender, CommandUtils.getListComponents(npc));
            }
        } else {
            sender.sendMessage(Messages.NO_NPCS);
        }
        return true;
    }
    
}
