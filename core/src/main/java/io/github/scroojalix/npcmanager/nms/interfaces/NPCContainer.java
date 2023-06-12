package io.github.scroojalix.npcmanager.nms.interfaces;

import com.comphenix.protocol.wrappers.PlayerInfoData;

import io.github.scroojalix.npcmanager.common.npc.NPCData;

public class NPCContainer {

    private NPCData npcData;
    private PlayerInfoData playerInfo;
    private int loaderTaskId;
    private NPCLoader loaderTask;
    private final int entityId;

    public NPCContainer(NPCData data, int id) {
        this.npcData = data;
        this.entityId = id;
    }

    public int getEntityId() {
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

    public int getLoaderTaskID() {
        return this.loaderTaskId;
    }

    public void setLoaderTaskID(int loaderTaskId) {
        this.loaderTaskId = loaderTaskId;
    }

    public NPCLoader getLoaderTask() {
        return this.loaderTask;
    }

    public void setLoaderTask(NPCLoader loaderTask) {
        this.loaderTask = loaderTask;
    }

}
