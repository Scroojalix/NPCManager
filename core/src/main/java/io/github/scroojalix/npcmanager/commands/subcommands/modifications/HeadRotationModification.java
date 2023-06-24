package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class HeadRotationModification extends SubCommand {

    public HeadRotationModification() {
        super(
            "hasHeadRotation",
            "Should the NPC look at nearby players.",
            "/npc modify <npc> hasHeadRotation <true|false>",
            true
        );
    }

    // TODO add extra headrotation option
    // none | perplayer | global
    // also add per NPC headrotation range/reset

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length >= 4) {
            if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
                NPCData data = PluginUtils.getNPCDataByName(args[1]);
                boolean value = args[3].equalsIgnoreCase("true");
                data.getTraits().setHeadRotation(value);
                sender.sendMessage(
                        PluginUtils.format("&6Set the head rotation of &F" + data.getName() + "&6 to &F" + value));
                main.npc.updateNPC(data);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if (args.length == 4) {
            result.add("true");
            result.add("false");
        }
        return result;
    }

}
