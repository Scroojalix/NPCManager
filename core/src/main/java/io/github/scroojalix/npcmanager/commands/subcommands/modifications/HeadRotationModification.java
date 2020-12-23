package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class HeadRotationModification extends SubCommand {

    @Override
    public String getName() {
        return "hasHeadRotation";
    }

    @Override
    public String getDescription() {
        return "Should the NPC look at nearby players.";
    }

    @Override
    public String getSyntax() {
        return "/npc modify <npc> hasHeadRotation <true|false>";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length >= 4) {
            if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
                NPCData data = main.npc.getNPCs().get(args[1]);
                boolean value = args[3].equalsIgnoreCase("true");
                data.getTraits().setHeadRotation(value);
                sender.sendMessage(PluginUtils.format("&6Set the head rotation of &F"+data.getName()+"&6 to &F"+value));
                main.npc.saveNPC(data);
                main.npc.updateNPC(data);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if(args.length == 4) {
            result.add("true");
            result.add("false");
        }
        return result;
    }
    
}
