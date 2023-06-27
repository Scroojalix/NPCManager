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

public class FlagsMeta {

    // TODO implement Glow Color flag

    public static class AddFlagMeta extends SubCommand {

        public AddFlagMeta() {
            super(
                "addflag",
                "Add a metadata flag to an NPC",
                "/npc modify <npc> metadata addflag <flag>",
                true
            );
        }

        @Override
        public boolean execute(NPCMain main, CommandSender sender, String[] args) {
            if (args.length < 5) return false;
            NPCData npc = PluginUtils.getNPCDataByName(args[1]);

            Flag flag = Flag.getFlagFromTag(args[4]);
            if (flag == null) {
                sender.sendMessage(PluginUtils.format("&c%s is not a valid flag.", args[4]));
                return false;
            }

            if (!npc.getTraits().getMetaInfo().hasFlag(flag)) {
                npc.getTraits().getMetaInfo().addFlag(flag);
                main.npc.updateNPC(npc);
                sender.sendMessage(PluginUtils.format("&6Added the &f%s &6flag for &f%s", flag.getCommandTag(), npc.getName()));
            } else {
                sender.sendMessage(PluginUtils.format("&c%s already has the &f%s &6flag", npc.getName(), flag.getCommandTag()));
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(String[] args) {           
            final List<String> result = new ArrayList<String>();
            if (!PluginUtils.npcExists(args[1])) return result;         
            final NPCMetaInfo meta = PluginUtils.getNPCDataByName(args[1]).getTraits().getMetaInfo();

            for (Flag flag : Flag.values()) {
                if (!meta.hasFlag(flag)) {
                    result.add(flag.getCommandTag());
                }
            }
            
            return result;
        }
    }

    public static class RemoveFlagMeta extends SubCommand {

        public RemoveFlagMeta() {
            super(
                "removeflag",
                "Remove a metadata flag from an NPC",
                "/npc modify <npc> metadata removeflag <flag>",
                true
            );
        }

        @Override
        public boolean execute(NPCMain main, CommandSender sender, String[] args) {
            if (args.length < 5) return false;
            NPCData npc = PluginUtils.getNPCDataByName(args[1]);

            Flag flag = Flag.getFlagFromTag(args[4]);
            if (flag == null) {
                sender.sendMessage(PluginUtils.format("&c%s is not a valid flag.", args[4]));
                return false;
            }

            if (npc.getTraits().getMetaInfo().hasFlag(flag)) {
                npc.getTraits().getMetaInfo().removeFlag(flag);
                main.npc.updateNPC(npc);
                sender.sendMessage(PluginUtils.format("&6Removed the &f%s &6flag for &f%s", flag.getCommandTag(), npc.getName()));
            } else {
                sender.sendMessage(PluginUtils.format("&c%s does not have the flag %s", npc.getName(), flag.getCommandTag()));
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(String[] args) {           
            final List<String> result = new ArrayList<String>();
            if (!PluginUtils.npcExists(args[1])) return result;         
            final NPCMetaInfo meta = PluginUtils.getNPCDataByName(args[1]).getTraits().getMetaInfo();

            for (Flag flag : Flag.values()) {
                if (meta.hasFlag(flag)) {
                    result.add(flag.getCommandTag());
                }
            }
            return result;
        }
    }    
}
