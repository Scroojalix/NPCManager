package io.github.scroojalix.npcmanager.utils.interactions;

import com.google.gson.annotations.Expose;

public class NPCInteractionData {

    @Expose
    private InteractEventType type;
    @Expose
    private String value;

    public NPCInteractionData(InteractEventType type, String value) {
        this.type = type;
        this.value = value;
    }

    public InteractEventType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }
}
