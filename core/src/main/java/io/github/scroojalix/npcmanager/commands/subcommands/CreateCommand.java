package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class CreateCommand extends SubCommand {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates an NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc create <name>";
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
        //TODO check that name is alphanumerical
        if (name.length() > 16) {
            sender.sendMessage(Messages.LONG_NAME);
            return true;
        } else if (!main.npc.getNPCs().containsKey(name)) {
            main.npc.createNPC(name, ((Player)sender).getLocation());
            sender.sendMessage(PluginUtils.format("&6Created an NPC named &F")+name);
            return true;
        }
        sender.sendMessage(Messages.NPC_EXISTS);
        return true;
    }
    
}