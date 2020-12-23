package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class SkinModification extends SubCommand {

    @Override
    public String getName() {
        return "skin";
    }

    @Override
    public String getDescription() {
        return "Customise the NPC's skin.";
    }

    @Override
    public String getSyntax() {
        return "/npc modify <npc> skin <skin>";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    //TODO redo this once new skin system is implemented.
    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length >= 4) {
            String value = args[3];
            if (value.equalsIgnoreCase("null") || NPCMain.instance.skinManager.values().contains(value)) {
                NPCData data = main.npc.getNPCs().get(args[1]);
                data.getTraits().setSkin(value.equalsIgnoreCase("null")?null:value);
                sender.sendMessage(PluginUtils.format("&6Set the range of &F"+data.getName()+"&6 to &F"+value));
                main.npc.saveNPC(data);
                main.npc.updateNPC(data);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED+"'"+value+"' is not a valid skin.");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if(args.length == 4) {
            result.add("null");
            for (String skin : NPCMain.instance.skinManager.values()) {
                result.add(skin);
            }
        }
        return result;
    }

}