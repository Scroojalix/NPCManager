package io.github.scroojalix.npcmanager.commands.subcommands.modifications.meta;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class HandStateMeta extends SubCommand {

    public HandStateMeta() {
        super(
            "handState",
            "Customise an NPC's hand state.",
            "/npc modify <npc> metadata handState <setHand | isRiptideSpin> <arg>",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 6) return false;
        NPCData npc = PluginUtils.getNPCDataByName(args[1]);
        if (args[4].equalsIgnoreCase("sethand")) {
            switch (args[5].toLowerCase()) {
                case "mainhand":
                npc.getTraits().getMetaInfo().getHandState().set(true, Hand.MAIN_HAND);
                main.npc.updateNPC(npc);
                sender.sendMessage(PluginUtils.format("&6Set the Hand State of &f%s &6to &f%s", npc.getName(), "Mainhand"));
                return true;

                case "offhand":
                npc.getTraits().getMetaInfo().getHandState().set(true, Hand.OFF_HAND);
                main.npc.updateNPC(npc);
                sender.sendMessage(PluginUtils.format("&6Set the Hand State of &f%s &6to &f%s", npc.getName(), "Offhand"));
                return true;

                case "none":
                npc.getTraits().getMetaInfo().getHandState().set(false, Hand.MAIN_HAND);
                main.npc.updateNPC(npc);
                sender.sendMessage(PluginUtils.format("&6Set the Hand State of &f%s &6to &f%s", npc.getName(), "None"));
                return true;

                default:
                sender.sendMessage(ChatColor.RED+"That is not a valid hand state.");
                return false;
            }
        } else if (args[4].equalsIgnoreCase("isRiptideSpin")) {
            if (args[5].equalsIgnoreCase("true")) {
                npc.getTraits().getMetaInfo().getHandState().setIsRiptideSpin(true);
                main.npc.updateNPC(npc);
                sender.sendMessage(PluginUtils.format("&6Activated Riptide Spin for &f%s", npc.getName()));
                return true;
            } else if (args[5].equalsIgnoreCase("false")) {
                npc.getTraits().getMetaInfo().getHandState().setIsRiptideSpin(false);
                main.npc.updateNPC(npc);
                sender.sendMessage(PluginUtils.format("&6Deactivated Riptide Spin for &f%s", npc.getName()));
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "That is not a valid boolean value.");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<>();
        
        if (args.length == 5) {
            result.add("setHand");
            result.add("isRiptideSpin");
        } else if (args.length == 6) {
            if (args[4].equalsIgnoreCase("sethand")) {
                result.add("mainhand");
                result.add("offhand");
                result.add("none");
            } else if (args[4].equalsIgnoreCase("isRiptideSpin")) {
                result.add("true");
                result.add("false");
            }
        }

        return result;
    }
    
}
