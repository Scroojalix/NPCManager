package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.chat.Messages;

public class ReloadCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin, or a specific NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc reload [npc]";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(ChatColor.GOLD + "Reloaded the plugin.");
            main.reloadPlugin();
            return true;
        } else if (main.npc.getNPCs().containsKey(args[1])) {
            main.npc.updateNPC(main.npc.getNPCs().get(args[1]));
            sender.sendMessage(PluginUtils.format("&6Reloaded &f"+args[1]));
            return true;
        } else {
            sender.sendMessage(ChatColor.RED+Messages.UNKNOWN_NPC);
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 2) {
            return getNPCs(args[1]);
        }
        return new ArrayList<String>();
    }
}
