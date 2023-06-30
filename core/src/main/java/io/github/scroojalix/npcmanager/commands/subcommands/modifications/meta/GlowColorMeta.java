package io.github.scroojalix.npcmanager.commands.subcommands.modifications.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.meta.MetaColor;
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

        MetaColor color = MetaColor.getMetaColorFromName(args[4]);
        if (color == null) {
            sender.sendMessage(PluginUtils.format("&c%s is not a valid Glow Color.", args[4]));
            return false;
        }

        npc.getTraits().getMetaInfo().setGlowColor(color);
        sender.sendMessage(PluginUtils.format("&6Set Glow Color of &f%s &6to &f%s", npc.getName(), color.getBukkitColor()+color.getColorName()));
        
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        final List<String> result = new ArrayList<String>();

        if (args.length != 5) return result;
        
        result.addAll(Arrays.asList(MetaColor.values()).stream()
        .map(c -> c.getColorName()).collect(Collectors.toList()));
        
        return result;    }
    
}
