package io.github.scroojalix.npcmanager.commands;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract boolean consoleCanRun();

    public abstract boolean execute(NPCMain main, CommandSender sender, String args[]);
    
}
