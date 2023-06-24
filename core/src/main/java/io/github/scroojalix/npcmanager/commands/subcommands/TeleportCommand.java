package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class TeleportCommand extends SubCommand {

    public TeleportCommand() {
        super(
            "tpto",
            "Teleport to an NPC.",
            "/npc tpto <npc>",
            false
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 2)
            return false;
        if (PluginUtils.npcExists(args[1])) {
            NPCData data = PluginUtils.getNPCDataByName(args[1]);
            ((Player) sender).teleport(data.getLoc());
            sender.sendMessage(PluginUtils.format("&6Teleported to &F" + data.getName()));
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + Messages.UNKNOWN_NPC);
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 2) {
            return getNPCs(args[1]);
        } else {
            return new ArrayList<String>();
        }
    }

}