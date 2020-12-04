package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.Messages;

public class RemoveCommand extends SubCommand {

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes an NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc remove <npc>";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 2)
            return false;
        String name = args[1];
        if (CommandUtils.npcExists(name, sender)) {
            main.npc.removeNPC(name, true);
            main.npc.getNPCs().remove(name);
            if (main.npc.getNPCs().isEmpty())
                main.npcFile.getConfig().set("npc", null);
            sender.sendMessage(Messages.REMOVE_NPC+name);
            return true;
        }
        return false;
    }
    
}
