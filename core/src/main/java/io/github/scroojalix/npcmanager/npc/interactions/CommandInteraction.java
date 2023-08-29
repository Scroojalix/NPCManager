package io.github.scroojalix.npcmanager.npc.interactions;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import io.github.scroojalix.npcmanager.NPCMain;

public class CommandInteraction extends InteractEvent {

    private final String command;
    private final boolean asConsole;

    public String getCommand() {
        return this.command;
    }

    public CommandInteraction(String command, boolean asConsole) {
        super(null);
        this.command = command;
        this.asConsole = asConsole;
    }

    @Override
    public void onInteract(InteractAtNPCEvent event) {
        NPCMain.instance.sendDebugMessage(Level.INFO, "Executed command: "+command);
        if (asConsole) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        } else {
            event.getPlayer().performCommand(command);
        }
    }
    
}