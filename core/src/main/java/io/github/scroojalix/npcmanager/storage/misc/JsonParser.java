package io.github.scroojalix.npcmanager.storage.misc;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.github.scroojalix.npcmanager.npc.NPCData;

public final class JsonParser {

    public static String toJson(NPCData data, boolean prettyPrinting) {
        GsonBuilder builder = new GsonBuilder()
		.disableHtmlEscaping();
		if (prettyPrinting) {
			builder.setPrettyPrinting();
		}
		return builder.create().toJson(data.serialise());
    }

    public static @Nullable NPCData fromJson(String name, String json, boolean prettyPrinting) throws Exception {
		if (json.isEmpty()) return null; // TODO imlement better error messages for empty files
		GsonBuilder builder = new GsonBuilder()
		.disableHtmlEscaping();
		if (prettyPrinting) {
			builder.setPrettyPrinting();
		}
		
		Map<String, Object> obj = builder.create().fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
		NPCData data = Serialisable.deserialise(obj, NPCData.class);
		data.setStored(true);
		return data;
    }
    
}
