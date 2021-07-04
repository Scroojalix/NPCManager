package io.github.scroojalix.npcmanager.common.storage.implementation;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.npc.NPCData;
import io.github.scroojalix.npcmanager.common.storage.implementation.interfaces.StorageImplementation;
import io.github.scroojalix.npcmanager.common.storage.misc.Serialisable;

public class TomlStorage implements StorageImplementation {

    private NPCMain main;

    public TomlStorage(NPCMain main) {
        this.main = main; 
    }

    @Override
    public String getImplementationName() {
        return "TOML";
    }

    @Override
    public void saveNPC(NPCData data) throws Throwable {
        TomlWriter writer = new TomlWriter.Builder()
            .indentValuesBy(2)
            .build();
        File file = new File(main.getDataFolder()+"/toml-storage", data.getName()+".toml");
        try {
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        //FIXME if the data contains an item stack with meta, stackoverflow error is thrown.
        writer.write(data.serialise(), file);
    }

    @Override
    public void removeNPC(String name) throws Throwable {
        File file = new File(main.getDataFolder()+"/toml-storage", name+".toml");
        if (file.exists()) {
            file.delete();
        }
    }

    //TODO abstract this out. It is very repetitive code.
    @Override
    @SuppressWarnings("unchecked")
    public void restoreNPCs() throws Throwable {
        if (new File(main.getDataFolder()+"/toml-storage").exists()) {
            File folder = new File(main.getDataFolder() + "/toml-storage");
            File[] npcFiles = folder.listFiles();
            if (npcFiles != null) {
                for (int i = 0; i < npcFiles.length; i++) {
                    File current = npcFiles[i];
                    if (current.isFile() && current.getName().endsWith(".toml")) {
                        try {   
                            Toml toml = new Toml().read(current);
                            NPCData data = Serialisable.deserialise(toml.to(Map.class), NPCData.class);
                            data.setStored(true);
                            if (data != null && data.getTraits() != null)
                                main.npc.spawnNPC(data);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
    }
    
}
