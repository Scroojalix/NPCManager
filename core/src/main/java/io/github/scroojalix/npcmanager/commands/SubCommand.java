package io.github.scroojalix.npcmanager.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public abstract class SubCommand {

    private final String name;
    private final String description;
    private final String syntax;
    private final boolean consoleCanRun;

    public SubCommand(String name, String description, String syntax, boolean consoleCanRun) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.consoleCanRun = consoleCanRun;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSyntax() {
        return syntax;
    }

    public boolean consoleCanRun() {
        return consoleCanRun;
    }

    public abstract boolean execute(NPCMain main, CommandSender sender, String args[]);

    public abstract List<String> onTabComplete(String[] args);

    protected List<String> filter(String arg, List<String> args) {
        List<String> result = new ArrayList<String>();
        for (String a : args) {
            if (a.toLowerCase().startsWith(arg.toLowerCase())) {
                result.add(a);
            }
        }
        return result;
    }

    protected List<String> getNPCs(String current) {
        List<String> result = new ArrayList<String>();
        for (String npc : PluginUtils.getAllNPCNames()) {
            if (npc.toLowerCase().startsWith(current.toLowerCase())) result.add(npc);
        }
        return result;
    }
    
}
