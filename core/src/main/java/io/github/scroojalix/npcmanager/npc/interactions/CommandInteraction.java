package io.github.scroojalix.npcmanager.npc.interactions;

import org.bukkit.Bukkit;

public class CommandInteraction implements InteractEvent {

    private String command;
    private boolean asConsole;

    public String getCommand() {
        return this.command;
    }

    public CommandInteraction(String command, boolean asConsole) {
        this.command = command;
        this.asConsole = asConsole;
    }

    @Override
    public String getInteractionName() {
        return null;
    }

    @Override
    public void onInteract(InteractAtNPCEvent event) {
        if (asConsole) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        } else {
            event.getPlayer().performCommand(command);
        }
    }
    
}