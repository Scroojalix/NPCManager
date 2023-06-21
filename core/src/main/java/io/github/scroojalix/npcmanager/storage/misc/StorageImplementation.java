package io.github.scroojalix.npcmanager.storage.misc;

import java.io.File;

import javax.annotation.Nonnull;

import io.github.scroojalix.npcmanager.npc.NPCData;

public interface StorageImplementation {
    
    /**
     * The display name for this storage implementation.
     * @return
     */
    public @Nonnull String getImplementationName();

    /**
     * Save an NPC via this storage implementation
     * @param data the npc to save 
     * @return {@code true} if the operation was successful, {@code false} otherwise.
     */
    public boolean saveNPC(@Nonnull NPCData data);

    /**
     * Remove an NPC with the name {@code name} from storage
     * @param name the NPC to remove from storage
     * @return {@code true} if the operation was successful, {@code false} otherwise.
     */
    public boolean removeNPC(@Nonnull String name);

    /**
     * Interface for Local Storage Options
     */
    public interface LocalStorage extends StorageImplementation {

        /**
         * Return the file extension for the file, which may be different
         * to the Implementation Name.
         * @return this implementations file extension.
         */
        public default String getFileExtension() {
            return getImplementationName().toLowerCase();
        }

        /**
         * Restore an NPC
         * @param file
         * @return {@code true} if the operation was successful, {@code false} otherwise.
         */
        public boolean restoreNPC(File file);
    }

    /**
     * Interface for Remote Storage Options
     */
    public interface RemoteStorage extends StorageImplementation {

        /**
         * Initialise this remote storage option.
         * @return {@code true} if the initialisation is successful, {@code false} otherwise.
         */
        public boolean init();
    
        /**
         * Shuts down this remote storage option.
         * @return {@code true} if the operation is successful, {@code false} otherwise.
         */
        public boolean shutdown();

        /**
         * Check if an NPC with the name {@code name} exists
         * @param name the name to check
         * @return {@code true} if the npc exists, {@code false} otherwise.
         */
        public boolean exists(@Nonnull String name);

        /**
         * Determine if the remote storage device is connected
         * @return {@code true} if connected, {@code false} otherwise.
         */
        public boolean isConnected();
        
        /**
         * Restores all NPCs from this remote storage device.
         * @return {@code true} if the operation was successful, {@code false} otherwise.
         * @throws RemoteStorageException if an error occurs
         */
        public boolean restoreAllNPCs();
    }
}
