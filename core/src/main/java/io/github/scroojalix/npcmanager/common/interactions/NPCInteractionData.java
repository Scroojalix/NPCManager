package io.github.scroojalix.npcmanager.common.interactions;

import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.common.storage.misc.Serialisable;

public class NPCInteractionData implements Serialisable {

    @Expose
    private InteractEventType type;
    @Expose
    private String value;

    NPCInteractionData() {}

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
