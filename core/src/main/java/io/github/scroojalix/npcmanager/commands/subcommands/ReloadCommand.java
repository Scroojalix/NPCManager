package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;

public class ReloadCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin.";
    }

    @Override
    public String getSyntax() {
        return "/npc reload";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD+"The plugin was reloaded.");
        main.reloadPlugin();
        return true;
    }
    
}
