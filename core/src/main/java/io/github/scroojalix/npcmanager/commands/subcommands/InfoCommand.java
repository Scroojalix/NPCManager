package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.NPCData;
import io.github.scroojalix.npcmanager.utils.NPCData.NPCTrait;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class InfoCommand extends SubCommand {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Provides info on an NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc info <name>";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    //TODO hide nulls
    //TODO Show interact event + other info
    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 2)
            return false;
        if (CommandUtils.npcExists(args[1], sender)) {
            NPCData data = main.npc.getNPCs().get(args[1]);
            NPCTrait traits = data.getTraits();
            sender.sendMessage(PluginUtils.format("&b&M&L                      &6 NPC Info &b&M&L                      "));
            sender.sendMessage(PluginUtils.format("&6Name: &F"+data.getName()));
            sender.sendMessage(PluginUtils.format("&6Display Name: &F"+traits.getDisplayName()));
            sender.sendMessage(PluginUtils.format("&6Subtitle: &F"+traits.getSubtitle()));
            CommandUtils.sendJSONMessage(sender, CommandUtils.getLocationComponents(args[1], data.getLoc()));
            sender.sendMessage(PluginUtils.format("&6Skin: &F"+traits.getSkin()));
            sender.sendMessage(PluginUtils.format("&6Range: &F"+traits.getRange()));
            sender.sendMessage(PluginUtils.format("&6Head Rotation: &F"+traits.hasHeadRotation()));
            sender.sendMessage(PluginUtils.format("&b&M&L                                                       "));
            return true;
        }
        return false;
    }
    
}
