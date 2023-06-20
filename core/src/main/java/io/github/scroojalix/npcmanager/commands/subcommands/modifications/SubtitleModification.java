package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class SubtitleModification extends SubCommand {

    @Override
    public String getName() {
        return "subtitle";
    }

    @Override
    public String getDescription() {
        return "Customise the subtitle of the NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc modify <npc> subtitle <text>";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length >= 4) {
            NPCData data = PluginUtils.getNPCDataByName(args[1]);
            String value = args[3];
            for (int arg = 4; arg < args.length; arg++) {
                value += " " + args[arg];
            }
            data.getTraits().setSubtitle(value);
            sender.sendMessage(PluginUtils.format("&6Set the subtitle of &F" + data.getName() + "&6 to &F" + value));
            main.storage.saveNPC(data);
            main.npc.updateNPC(data);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if (args.length == 4) {
            result.add("null");
        }
        return result;
    }

}
