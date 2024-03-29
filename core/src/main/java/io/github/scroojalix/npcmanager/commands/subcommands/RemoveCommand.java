package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;

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
            sender.sendMessage(ChatColor.GOLD+"Removed "+ChatColor.WHITE+name);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 2) {
            return getNPCs(args[1]);
        } else {
            return new ArrayList<String>();
        }
    }
    
}
