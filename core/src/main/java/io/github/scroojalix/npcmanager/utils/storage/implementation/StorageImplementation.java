package io.github.scroojalix.npcmanager.utils.storage.implementation;

import java.util.Set;

import io.github.scroojalix.npcmanager.utils.dependencies.Dependency;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public interface StorageImplementation {
    
    public void init() throws Throwable;

    public void shutdown() throws Throwable;

    public void saveNPC(NPCData data);

    public void removeNPC(String name);
    
    public void restoreNPCs();

    public Set<Dependency> getDependencies();

}
