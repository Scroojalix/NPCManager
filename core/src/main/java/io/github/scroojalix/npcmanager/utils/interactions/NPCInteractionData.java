package io.github.scroojalix.npcmanager.utils.interactions;

import java.util.Map;

import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.storage.misc.Serialisable;

public class NPCInteractionData implements Serialisable {

    @Expose
    private InteractEventType type;
    @Expose
    private String value;

	@Override
	public Map<String, Object> serialise() {
        return PluginUtils.serialise(this);
	}

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
