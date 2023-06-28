package io.github.scroojalix.npcmanager.commands.subcommands.modifications.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.meta.Flag;
import io.github.scroojalix.npcmanager.npc.meta.GlowColor;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class GlowColorMeta extends SubCommand {

    public GlowColorMeta() {
        super(
            "glowColor",
            "Customise an NPC's glow color, if it is enabled.",
            "/npc modify <npc> metadata glowColor <GlowColor>",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 5) return false;
        NPCData npc = PluginUtils.getNPCDataByName(args[1]);

        GlowColor color = GlowColor.getGlowColorFromName(args[4]);
        if (color == null) {
            sender.sendMessage(PluginUtils.format("&c%s is not a valid Glow Color.", args[4]));
            return false;
        }

        npc.getTraits().getMetaInfo().setGlowColor(color);
        main.npc.updateNPC(npc);
        sender.sendMessage(PluginUtils.format("&6Set Glow Color of &f%s &6to &f%s", npc.getName(), color.getBukkitColor()+color.getColorName()));

        if (!npc.getTraits().getMetaInfo().hasFlag(Flag.GLOWING)) {
            sender.sendMessage(PluginUtils.format("&cAdd the &e%s &cflag to see the effect of this.", Flag.GLOWING.getCommandTag()));
        }
        
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        final List<String> result = new ArrayList<String>();

        if (args.length != 5) return result;
        
        result.addAll(Arrays.asList(GlowColor.values()).stream()
        .map(c -> c.getColorName()).collect(Collectors.toList()));
        
        return result;    }
    
}
