package io.github.scroojalix.npcmanager.common.storage.implementation.interfaces;

public interface RemoteStorage {

    public void init() throws Throwable;
    
    public void shutdown() throws Throwable;

    public boolean exists(String name);

    public boolean isConnected();

}
