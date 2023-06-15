package io.github.scroojalix.npcmanager.npc.skin;

import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

public class SkinData implements Serialisable {

    @Expose
    private SkinType type;
    @Expose
    private String name;
    @Expose
    private String uuid;
    @Expose
    private String texture;
    @Expose
    private String signature;
    @Expose
    private boolean keepLatest;
    
    private boolean hasUpdated;

    SkinData() {}
    
    public SkinData(SkinType type, String name, String uuid, String texture, String signature, boolean keepLatest) {
        this.type = type;
        this.name = name;
        this.uuid = uuid;
        this.texture = texture;
        this.signature = signature;
        this.keepLatest = keepLatest;
    }

    public boolean needsUpdating() {
        return !hasUpdated && keepLatest && type == SkinType.PLAYER;
    }

    public String getSkinName() {
        return name;
    }
    
    public String getUUID() {
        return this.uuid;
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
    
    public String getSignature() {
        return this.signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public String getTexture() {
        return this.texture;
    }
    
    public void setTexture(String texture) {
        this.texture = texture;
    }
    
    public boolean keepLatest() {
        return this.keepLatest;
    }
    
    public void setKeepLatest(boolean keepLatest) {
        this.keepLatest = keepLatest;
    }
    
    public void setHasUpdated(boolean hasUpdated) {
        this.hasUpdated = hasUpdated;
    }
}
