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
        boolean flag = false;
        if (args[5].equalsIgnoreCase("true")) {
            flag = true;
        } else if (!args[5].equalsIgnoreCase("false")) {
            sender.sendMessage(ChatColor.RED + "That is not a valid boolean value.");
            return false;
        }
        
        boolean successful = false;
        switch (args[4].toLowerCase()) {
            case "onfire":
                npc.getTraits().getMetaInfo().setOnFire(flag);
                successful = true;
                break;
            case "invisible":
                npc.getTraits().getMetaInfo().setInvisible(flag);
                successful = true;
                break;
            case "glowenabled":
                npc.getTraits().getMetaInfo().setGlowing(flag);
                successful = true;
                break;
            case "elytraenabled":
                npc.getTraits().getMetaInfo().setElytraFlying(flag);
                successful = true;
                break;
            case "shivering":
                npc.getTraits().getMetaInfo().setShivering(flag);
                successful = true;
                break;
            case "collisionenabled":
                npc.getTraits().getMetaInfo().setCollision(flag);
                successful = true;
                break;

            case "glowcolor":
                ChatColor color;
                try {
                    color = ChatColor.valueOf(args[4]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid glow color.");
                    return false;
                }
                npc.getTraits().getMetaInfo().setGlowColor(color);
                successful = true;
                break;
        }

        if (successful) {
            main.npc.updateNPC(npc);
            sender.sendMessage(PluginUtils.format("&6Set &f%s &6flag to &f%s", args[4], args[5]));
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "That is not a valid flag and/or value.");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<>();

        if (args.length == 5) {
            result.add("onFire");
            result.add("invisible");
            result.add("glowEnabled");
            result.add("elytraEnabled");
            result.add("shivering");
            result.add("glowColor");
            result.add("collisionEnabled");
        } else if (args.length == 6) {
            switch(args[4].toLowerCase()) {
                case "onfire":
                case "invisible":
                case "glowenabled":
                case "elytraenabled":
                case "shivering":
                case "collisionenabled":
                    result.add("true");
                    result.add("false");
                break;
                case "glowcolor":
                    result.addAll(Arrays.stream(ChatColor.values())
                    .map(f -> f.name()).collect(Collectors.toList()));
                    result.remove("MAGIC");
                    result.remove("BOLD");
                    result.remove("STRIKETHROUGH");
                    result.remove("UNDERLINE");
                    result.remove("ITALIC");
                    result.remove("RESET");
                break;
            }
        }
        return filter(args[args.length - 1], result);
    }
    
}
