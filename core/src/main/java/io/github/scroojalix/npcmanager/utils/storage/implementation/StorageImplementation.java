package io.github.scroojalix.npcmanager.utils.storage.implementation;

import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public interface StorageImplementation {

    public String getImplementationName();

    public boolean isRemote();
    
    public void init() throws Throwable;

    public void shutdown() throws Throwable;

    public void saveNPC(NPCData data) throws Throwable;

    public void removeNPC(String name) throws Throwable;
    
    public void restoreNPCs() throws Throwable;
}
