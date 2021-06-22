package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.common.PluginUtils;
import io.github.scroojalix.npcmanager.common.npc.NPCData;

public class MoveCommand extends SubCommand {

    @Override
    public String getName() {
        return "move";
    }

    @Override
    public String getDescription() {
        return "Moves an NPC to your current location.";
    }

    @Override
    public String getSyntax() {
        return "/npc move <npc>";
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
        if (CommandUtils.npcExists(name, sender)) {
            NPCData data = main.npc.getNPCs().get(name);
            main.npc.moveNPC(data, ((Player) sender).getLocation());
            sender.sendMessage(PluginUtils.format("&6Moved &F") + name + PluginUtils.format("&6 to your position."));
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
