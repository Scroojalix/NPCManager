package io.github.scroojalix.npcmanager.commands.subcommands.modifications.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.meta.Flag;
import io.github.scroojalix.npcmanager.npc.meta.GlowColor;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class FlagsMeta extends SubCommand {

    public FlagsMeta() {
        super(
            "flags",
            "Customise various flags for an NPC.",
            "/npc modify <npc> metadata flags <flag> <value>",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 6) return false;
        NPCData npc = PluginUtils.getNPCDataByName(args[1]);

        Flag flag = Flag.getFlagFromTag(args[4]);
        if (flag == null) {
            sender.sendMessage(PluginUtils.format("&c%s is not a valid flag.", args[4]));
            return false;
        }

        Object value;
        if (flag != Flag.GLOW_COLOR) {
            if (args[5].equalsIgnoreCase("true")) {
                value = true;
            } else if (!args[5].equalsIgnoreCase("false")) {
                sender.sendMessage(ChatColor.RED + "That is not a valid boolean value.");
                return false;
            } else {
                value = false;
            }
        } else {
            try {
                value = GlowColor.valueOf(args[5]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "That is not a valid glow color.");
                return false;
            }
        }
        npc.getTraits().getMetaInfo().setFlag(flag, value);
        main.npc .updateNPC(npc);

        sender.sendMessage(PluginUtils.format("&6Set &f%s &6flag to &f%s", args[4], args[5]));
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<>();

        if (args.length == 5) {
            for (Flag flag : Flag.values()) {
                if (flag.isEnabled()) {
                    result.add(flag.getCommandTag());
                }
            }
        } else if (args.length == 6) {

            Flag flag = Flag.getFlagFromTag(args[4]);
            if (flag == null) return result;
            
            if (flag.getValueClass().equals(Boolean.class)) {
                result.add("true");
                result.add("false");
            } else if (flag.getValueClass().equals(GlowColor.class)) {
                result.addAll(Arrays.stream(GlowColor.values())
                    .map(f -> f.name()).collect(Collectors.toList()));
            }
        }
        return filter(args[args.length - 1], result);
    }
    
}
