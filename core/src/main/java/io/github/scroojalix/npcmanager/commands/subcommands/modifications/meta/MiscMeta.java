package io.github.scroojalix.npcmanager.commands.subcommands.modifications.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.meta.ModifiableMetadata;
import io.github.scroojalix.npcmanager.npc.meta.NPCMetaInfo;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class MiscMeta extends SubCommand {

    public MiscMeta() {
        super(
            "misc",
            "Modify miscellaneous NPC metadata values",
            "/npc modify <npc> metadata misc <key> <value>",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 6) return false;
        NPCMetaInfo npcMeta = PluginUtils.getNPCDataByName(args[1]).getTraits().getMetaInfo();
        ModifiableMetadata updating = PluginUtils.getEnumFromName(args[4], ModifiableMetadata.class);
        if (updating == null) {
            sender.sendMessage(PluginUtils.format("&c%s is not a valid Meta Type.", args[4]));
            return false;
        }
        Object value = args[5];
        if (updating.getValueClass().isEnum()) 
            value = PluginUtils.getEnumFromName(args[5], updating.getValueClass().asSubclass(Enum.class));
        
        sender.sendMessage("Updating "+updating.name()+ " to "+value);
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 5) {
            result.addAll(Arrays.stream(ModifiableMetadata.values())
            .map(f -> f.getName()).collect(Collectors.toList()));
        } else if (args.length == 6) {
            ModifiableMetadata meta = PluginUtils.getEnumFromName(args[4], ModifiableMetadata.class);
            if (meta != null && meta.getValueClass().isEnum()) {
                for (Object o : meta.getValueClass().getEnumConstants()) {
                    result.add(o.toString());
                }
            }
        }
        return result;
    }
    
}
