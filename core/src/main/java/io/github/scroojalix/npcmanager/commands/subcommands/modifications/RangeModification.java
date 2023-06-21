package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class RangeModification extends SubCommand {

    @Override
    public String getName() {
        return "range";
    }

    @Override
    public String getDescription() {
        return "Customise the NPC's range.";
    }

    @Override
    public String getSyntax() {
        return "/npc modify <npc> range <value>";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length >= 4) {
            try {
                NPCData data = PluginUtils.getNPCDataByName(args[1]);
                int value = Integer.parseInt(args[3]);
                if (value <= 0) {
                    sender.sendMessage(ChatColor.RED + "Range must be above 0.");
                    return false;
                }
                data.getTraits().setRange(value);
                sender.sendMessage(PluginUtils.format("&6Set the range of &F" + data.getName() + "&6 to &F" + value));
                main.npc.updateNPC(data);
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "'" + args[3] + "' is not a number.");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        return new ArrayList<String>();
    }

}
