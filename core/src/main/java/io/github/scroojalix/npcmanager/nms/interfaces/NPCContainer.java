package io.github.scroojalix.npcmanager.nms.interfaces;

import org.bukkit.Location;

import com.comphenix.protocol.wrappers.PlayerInfoData;

import io.github.scroojalix.npcmanager.common.npc.NPCData;

public class NPCContainer {

    //NPC
    private NPCData npcData;
    private PlayerInfoData playerInfo;
    private final int entityId;

    //Holograms
    //TODO make this into an array to allow multiple lines

    private boolean nameHoloEnabled;
    private int nameHoloId;
    private Location nameHoloLoc;

    private boolean subtitleHoloEnabled;
    private int subtitleHoloId;
    private Location subtitleHoloLoc;
    
    //Loader Task Info
    private int loaderTaskId;
    private NPCLoader loaderTask;

    public NPCContainer(NPCData data, int id) {
        this.npcData = data;
        this.entityId = id;
    }

    // NPC

    public int getNPCEntityID() {
        return this.entityId;
    }

    public NPCData getNPCData() {
        return this.npcData;
    }

    public void setNPCData(NPCData npcData) {
        this.npcData = npcData;
    }

    public PlayerInfoData getPlayerInfo() {
        return this.playerInfo;
    }

    public void setPlayerInfo(PlayerInfoData playerInfo) {
        this.playerInfo = playerInfo;
    }

    // Holograms

    public void disableNameHolo() {
        this.nameHoloEnabled = false;
        this.nameHoloId = 0;
        this.nameHoloLoc = null;
    }

    public void setNameHolo(boolean enabled, int id, Location loc) {
        this.nameHoloEnabled = enabled;
        this.nameHoloId = id;
        this.nameHoloLoc = loc;
    }

    public void disableSubtitleHolo() {
        this.subtitleHoloEnabled = false;
        this.subtitleHoloId = 0;
        this.subtitleHoloLoc = null;
    }
    
    public void setSubtitleHolo(boolean enabled, int id, Location loc) {
        this.subtitleHoloEnabled = enabled;
        this.subtitleHoloId = id;
        this.subtitleHoloLoc = loc;
    }

    public boolean isNameHoloEnabled() {
        return this.nameHoloEnabled;
    }
    
    public int getNameHoloID() {
        return nameHoloId;
    }
    
    public Location getNameHoloLocation() {
        return nameHoloLoc;
    }

    public boolean isSubtitleHoloEnabled() {
        return this.subtitleHoloEnabled;
    }

    public int getSubtitleHoloID() {
        return subtitleHoloId;
    }

    public Location getSubtitleHoloLocation() {
        return subtitleHoloLoc;
    }

    // Loader Task

    public void setLoaderTask(NPCLoader loaderTask, int loaderTaskId) {
        this.loaderTask = loaderTask;
        this.loaderTaskId = loaderTaskId;
    }

    public int getLoaderTaskID() {
        return this.loaderTaskId;
    }

    public NPCLoader getLoaderTask() {
        return this.loaderTask;
    }
}
