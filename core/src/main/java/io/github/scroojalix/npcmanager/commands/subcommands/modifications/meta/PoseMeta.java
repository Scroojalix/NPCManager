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
import io.github.scroojalix.npcmanager.npc.NPCMetaInfo.Pose;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class PoseMeta extends SubCommand {

    public PoseMeta() {
        super("pose",
        "Customise an NPC's pose.",
        "/npc modify <npc> metadata pose <pose>",
        true);
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        sender.sendMessage("Attempting to change pose");
        if (args.length < 5) return false;

        NPCData npc = PluginUtils.getNPCDataByName(args[1]);
        Pose pose;
        try {
            pose = Pose.valueOf(args[4]);
        } catch(IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "That is not a valid pose! Try again.");
            return false;
        }

        npc.getTraits().getMetaInfo().setPose(pose);
        main.npc.updateNPC(npc);
        sender.sendMessage(PluginUtils.format("&6Set pose of &f%s &6to &f%s", npc.getName(), pose));
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();

        if (args.length == 5) {
            result.addAll(Arrays.stream(Pose.values()).map(f -> f.name()).collect(Collectors.toList()));
        }
        
        return filter(args[4], result);
    }

}
