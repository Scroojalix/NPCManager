package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.modifications.meta.*;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class MetadataModification extends SubCommand {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public MetadataModification() {
        super(
            "metadata",
            "Customise the metadata of an NPC.",
            "/npc modify <npc> metadata <fields | flags | skinLayers | reset> [args...]",
            true
        );
        subcommands.add(new FlagsMeta());
        subcommands.add(new SkinLayersMeta());
        subcommands.add(new FieldsMeta());
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        NPCData npc = PluginUtils.getNPCDataByName(args[1]);
        if (args.length >= 4) {
            if (args[3].equalsIgnoreCase("reset")) {
                npc.getTraits().clearMetaInfo();
                main.npc.updateNPCPackets(npc);
                sender.sendMessage(PluginUtils.format("&6Reset metadata for &f%s", npc.getName()));
                return true;
            }
            for (SubCommand command : subcommands) {
                if (!args[3].equalsIgnoreCase(command.getName())) continue;
                if (command.consoleCanRun() || sender instanceof Player) {
                    if (!command.execute(main, sender, args)) {
                        sender.sendMessage(ChatColor.RED + "Usage: " + command.getSyntax());
                    } else {
                        main.npc.updateNPCPackets(npc);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Sorry console, but you can't do that.");
                }
                return true;
            }
            return false;
        }
        for (SubCommand sub : subcommands) {
            sender.sendMessage(PluginUtils.format("&B" + sub.getSyntax() + " &F&L-&6 " + sub.getDescription()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();

        if (args.length == 4) {
            for (SubCommand sub : subcommands) {
                result.add(sub.getName());
            }
            result.add("reset");
        } else if (args.length >= 5) {
            for (SubCommand sub : subcommands) {
                if (args[3].equalsIgnoreCase(sub.getName())) {
                    result = sub.onTabComplete(args);
                    break;
                }
            }
        }
        return filter(args[args.length-1], result);
    }
    
}
