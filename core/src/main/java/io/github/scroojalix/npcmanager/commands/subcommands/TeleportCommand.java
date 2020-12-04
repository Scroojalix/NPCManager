package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class TeleportCommand extends SubCommand {

    @Override
    public String getName() {
        return "tpto";
    }

    @Override
    public String getDescription() {
        return "Teleport to an NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc tpto <npc>";
    }

    @Override
    public boolean consoleCanRun() {
        return false;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 2)
            return false;
        if (main.npc.getNPCs().containsKey(args[1])) {
            NPCData data = main.npc.getNPCs().get(args[1]);
            ((Player)sender).teleport(data.getLoc());
            sender.sendMessage(PluginUtils.format("&6Teleported to NPC &F"+data.getName()));
            return true;
        } else {
            sender.sendMessage(Messages.UNKNOWN_NPC);
            return true;
        }
    }

}