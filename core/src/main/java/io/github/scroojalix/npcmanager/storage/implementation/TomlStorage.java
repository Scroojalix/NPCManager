package io.github.scroojalix.npcmanager.storage.implementation;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.storage.misc.Serialisable;
import io.github.scroojalix.npcmanager.storage.misc.StorageImplementation;
import io.github.scroojalix.npcmanager.utils.Messages;

public class TomlStorage implements StorageImplementation.LocalStorage {

    private NPCMain main;

    public TomlStorage(NPCMain main) {
        this.main = main; 
    }

    @Override
    public @Nonnull String getImplementationName() {
        return "TOML";
    }

    @Override
    public boolean saveNPC(@Nonnull NPCData data) {
        TomlWriter writer = new TomlWriter.Builder()
            .indentValuesBy(2)
            .build();
        File file = new File(main.getDataFolder()+"/toml-storage", data.getName()+".toml");
        try {
			file.getParentFile().mkdirs();
			file.createNewFile();
            writer.write(data.serialise(), file);
            return true;
		} catch (IOException e) {
            main.getLogger().severe(e.getMessage());
            return false;
		}
    }

    @Override
    public boolean removeNPC(@Nonnull String name) {
        File file = new File(main.getDataFolder()+"/toml-storage", name+".toml");
        if (!file.exists()) return false;
        return file.delete();
    }

    @Override
    public boolean restoreNPC(File current) {
        try {
            Toml toml = new Toml().read(current);
            NPCData data = Serialisable.deserialise(toml.toMap(), NPCData.class);
            data.setStored(true);
            if (data != null && data.getTraits() != null)
                main.npc.spawnNPC(data);
            return true;
        } catch (Exception e) {
            Messages.printNPCRestoreError(main, current.getName(), e.getMessage());
        }
        return false;
    }
}
