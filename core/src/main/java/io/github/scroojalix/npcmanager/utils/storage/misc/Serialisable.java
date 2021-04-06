package io.github.scroojalix.npcmanager.utils.storage.misc;

import java.util.Map;

public abstract interface Serialisable {

    public abstract Map<String, Object> serialise();
    
}
