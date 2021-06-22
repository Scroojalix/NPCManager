package io.github.scroojalix.npcmanager.common.storage.implementation.interfaces;

import io.github.scroojalix.npcmanager.common.npc.NPCData;

public interface StorageImplementation {

    public String getImplementationName();

    public void saveNPC(NPCData data) throws Throwable;

    public void removeNPC(String name) throws Throwable;
    
    public void restoreNPCs() throws Throwable;
}
