package io.github.scroojalix.npcmanager.npc.interactions;

public enum InteractEventType {
    CONSOLE_COMMAND("[Console Command]", "consoleCommand"),
    PLAYER_COMMAND("[Player Command]", "playerCommand"),
    CUSTOM("&b[Custom]", "custom"),
    NONE("[None]", "none");

    public final String infoPrefix;
    public final String commandString;

    private InteractEventType(String infoPrefix, String commandString) {
        this.infoPrefix = infoPrefix;
        this.commandString = commandString;
    }
}