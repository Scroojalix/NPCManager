package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class RenameCommand extends SubCommand {

    public RenameCommand() {
        super(
            "rename",
            "Rename an NPC.",
            "/npc rename <npc> <name>",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 3)
            return false;
        if (CommandUtils.npcExists(args[1], sender)) {
            NPCData data = PluginUtils.getNPCDataByName(args[1]);
            if (args[1].equals(args[2])) {
                sender.sendMessage(ChatColor.RED + "You cannot rename an NPC to its previous name!");
                return false;
            }
            if (!PluginUtils.npcExists(args[2])) {
                if (PluginUtils.isAlphanumeric(args[2])) {
                    main.npc.renameNPC(data, args[2]);
                    sender.sendMessage(PluginUtils.format("&6Renamed &f" + args[1] + " &6to &f" + args[2]));
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + Messages.NOT_ALPHANUMERIC);
                }
            } else {
                sender.sendMessage(ChatColor.RED + Messages.NPC_EXISTS);
            }
        }
        return false;
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
