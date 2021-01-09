package io.github.scroojalix.npcmanager.utils.storage.implementation;

import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public interface StorageImplementation {
    
    public void init() throws Exception;

    public void shutdown() throws Exception;

    public void saveNPC(NPCData data);

    public void removeNPC(String name);
    
    public void restoreNPCs();

}
