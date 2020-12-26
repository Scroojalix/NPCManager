package io.github.scroojalix.npcmanager.utils.npc.skin;

import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class SkinData {

    @Expose
    private String skinName;
    @Expose
    private String texture;
    @Expose
    private String signature;
    @Expose
    private boolean keepLatest;
    
    private boolean hasUpdated;
    
    public SkinData(String skinName, String texture, String signature, boolean keepLatest) {
        this.skinName = skinName;
        this.texture = texture;
        this.signature = signature;
        this.keepLatest = keepLatest;
    }
    
    public boolean needsUpdating() {
        return !hasUpdated && keepLatest() && PluginUtils.isAlphanumeric(skinName) && skinName.length() <= 16;
    }
    
    public String getSkinName() {
        return this.skinName;
    }
    
    public void setSkinName(String skinName) {
        this.skinName = skinName;
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
