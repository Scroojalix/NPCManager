package io.github.scroojalix.npcmanager.commands.subcommands.modifications.meta;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.meta.Flag;
import io.github.scroojalix.npcmanager.npc.meta.NPCMetaInfo;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class FlagsMeta extends SubCommand {

    public FlagsMeta() {
        super(
            "flags",
            "Add/remove metadata flags for an NPC.",
            "/npc modify <npc> metadata flags <add | remove> <flag>",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 6) return false;
        NPCData npc = PluginUtils.getNPCDataByName(args[1]);

        Flag flag = Flag.getFlagFromTag(args[5]);
        if (flag == null) {
            sender.sendMessage(PluginUtils.format("&c%s is not a valid flag.", args[4]));
            return false;
        }

        if (args[4].equals("add")) {
            if (!npc.getTraits().getMetaInfo().hasFlag(flag)) {
                npc.getTraits().getMetaInfo().addFlag(flag);
                sender.sendMessage(PluginUtils.format("&6Added the &f%s &6flag for &f%s", flag.toString(), npc.getName()));
            } else {
                sender.sendMessage(PluginUtils.format("&c%s already has the &f%s &6flag", npc.getName(), flag.toString()));
            }
        } else if (args[4].equals("remove")) {
            if (npc.getTraits().getMetaInfo().hasFlag(flag)) {
                npc.getTraits().getMetaInfo().removeFlag(flag);
                sender.sendMessage(PluginUtils.format("&6Removed the &f%s &6flag for &f%s", flag.toString(), npc.getName()));
            } else {
                sender.sendMessage(PluginUtils.format("&c%s does not have the flag %s", npc.getName(), flag.toString()));
            }            
        } else {
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();

        if (args.length == 5) {
            result.add("add");
            result.add("remove");
        } else if (args.length == 6) {
            if (!PluginUtils.npcExists(args[1])) return result;         
            final NPCMetaInfo meta = PluginUtils.getNPCDataByName(args[1]).getTraits().getMetaInfo();

            if (args[4].equalsIgnoreCase("add")) {
                for (Flag flag : Flag.values()) {
                    if (!meta.hasFlag(flag)) {
                        result.add(flag.toString());
                    }
                }
            } else if (args[4].equalsIgnoreCase("remove")) {
                for (Flag flag : Flag.values()) {
                    if (meta.hasFlag(flag)) {
                        result.add(flag.toString());
                    }
                }
            }
        }

        return result;
    }  
}
