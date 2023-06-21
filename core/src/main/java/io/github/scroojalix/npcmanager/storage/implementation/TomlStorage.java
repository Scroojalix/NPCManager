package io.github.scroojalix.npcmanager.storage.implementation;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.storage.implementation.interfaces.StorageImplementation;
import io.github.scroojalix.npcmanager.storage.misc.Serialisable;
import io.github.scroojalix.npcmanager.utils.Messages;

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
                            NPCData data = Serialisable.deserialise(toml.toMap(), NPCData.class);
                            data.setStored(true);
                            if (data != null && data.getTraits() != null)
                                main.npc.spawnNPC(data);
                        } catch (JsonSyntaxException e) {
                            NPCMain.instance.getLogger().severe(Messages.getNPCRestoreError(current.getName(), "Invalid JSON"));
                            NPCMain.instance.getLogger().severe(Messages.RESOLVE_ERRORS);
                        } catch (IllegalArgumentException | NullPointerException e) {
                            NPCMain.instance.getLogger().severe(Messages.getNPCRestoreError(current.getName(), e.getMessage()));
                            NPCMain.instance.getLogger().severe(Messages.RESOLVE_ERRORS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
    }
    
}
