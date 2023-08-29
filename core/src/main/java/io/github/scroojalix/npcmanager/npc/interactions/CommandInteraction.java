package io.github.scroojalix.npcmanager.npc.interactions;

import org.bukkit.Bukkit;

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
        if (asConsole) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        } else {
            event.getPlayer().performCommand(command);
        }
    }
    
}