package io.github.scroojalix.npcmanager.utils.storage.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.storage.implementation.interfaces.StorageImplementation;

public class JsonStorage implements StorageImplementation {

    private NPCMain main;

    public JsonStorage(NPCMain main) {
        this.main = main;
    }

    @Override
    public String getImplementationName() {
        return "JSON";
    }

    @Override
    public void saveNPC(NPCData data) {
        File jsonFile = new File(main.getDataFolder()+"/json-storage", data.getName()+".json");
		try {
			jsonFile.getParentFile().mkdirs();
			jsonFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter writer = new FileWriter(jsonFile);
			writer.write(data.toJson(true));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void removeNPC(String name) {
        File jsonFile = new File(main.getDataFolder()+"/json-storage", name+".json");
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }

    @Override
    public void restoreNPCs() {
        if (new File(main.getDataFolder()+"/json-storage").exists()) {
            File jsonStorage = new File(main.getDataFolder() + "/json-storage");
            File[] npcFiles = jsonStorage.listFiles();
            if (npcFiles != null) {
                for (int i = 0; i < npcFiles.length; i++) {
                    File current = npcFiles[i];
                    if (current.isFile() && current.getName().endsWith(".json")) {
                        try {
                            String json = new String(Files.readAllBytes(Paths.get(current.getPath())));
                            NPCData data = NPCData.fromJson(current.getName().replace(".json", ""), json, true);
                            if (data != null)
                                main.npc.spawnNPC(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
