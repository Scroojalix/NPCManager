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
        NPCMetaInfo meta = PluginUtils.getNPCDataByName(args[1]).getTraits().getMetaInfo();

        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 5) {
            result.addAll(Arrays.stream(ModifiableMetadata.values())
            .map(f -> f.getName()).collect(Collectors.toList()));
        } else if (args.length == 6) {
            ModifiableMetadata meta = ModifiableMetadata.getFromTag(args[4]);
            if (meta != null && meta.getValueClass().isEnum()) {
                for (Object o : meta.getValueClass().getEnumConstants()) {
                    result.add(o.toString());
                }
            }
        }
        return result;
    }
    
}
