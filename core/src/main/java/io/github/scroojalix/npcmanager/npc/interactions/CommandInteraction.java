package io.github.scroojalix.npcmanager.npc.interactions;

public class CommandInteraction implements InteractEvent {

    private String command;

    public String getCommand() {
        return this.command;
    }

    public CommandInteraction(String command) {
        this.command = command;
    }

    @Override
    public String getInteractionName() {
        return null;
    }

    @Override
    public void onInteract(InteractAtNPCEvent event) {
        event.getPlayer().performCommand(command);
    }
    
}