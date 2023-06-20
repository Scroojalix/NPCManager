package io.github.scroojalix.npcmanager.npc.interactions;

public enum InteractEventType {
    COMMAND("&d[Command] /"),
    CUSTOM("&b[Custom] ");

    public final String infoPrefix;

    private InteractEventType(String infoPrefix) {
        this.infoPrefix = infoPrefix;
    }
}