package io.github.scroojalix.npcmanager.storage.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.storage.misc.JsonParser;
import io.github.scroojalix.npcmanager.storage.misc.StorageImplementation;
import io.github.scroojalix.npcmanager.utils.Messages;

public class JsonStorage implements StorageImplementation.LocalStorage {

    private NPCMain main;

    public JsonStorage(NPCMain main) {
        this.main = main;
    }

    @Override
    public @Nonnull String getImplementationName() {
        return "JSON";
    }

    @Override
    public boolean saveNPC(@Nonnull NPCData data) {
        File jsonFile = new File(main.getDataFolder()+"/json-storage", data.getName()+".json");
		try {
			jsonFile.getParentFile().mkdirs();
			jsonFile.createNewFile();
			FileWriter writer = new FileWriter(jsonFile);
			writer.write(JsonParser.toJson(data, true));
			writer.close();
            return true;
		} catch (IOException e) {
			main.getLogger().severe(e.getMessage());
            return false;
		}
    }
    
    @Override
    public boolean removeNPC(@Nonnull String name) {
        File jsonFile = new File(main.getDataFolder()+"/json-storage", name+".json");
        if (!jsonFile.exists()) return false;
        return jsonFile.delete();
    }

    @Override
    public boolean restoreNPC(File file) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(file.getPath())));
            NPCData data = JsonParser.fromJson(file.getName().replaceFirst("[.][^.]+$", ""), json, true);
            if (data == null) return false;

            main.npc.spawnNPC(data);
            return true;
        } catch (Exception e) {
            Messages.printNPCRestoreError(main, file.getName(), e.getMessage());
            return false;
        }
    }
}
