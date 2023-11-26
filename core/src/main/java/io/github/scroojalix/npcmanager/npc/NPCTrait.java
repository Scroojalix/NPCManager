package io.github.scroojalix.npcmanager.npc;

import javax.annotation.Nonnull;

import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.npc.equipment.NPCEquipment;
import io.github.scroojalix.npcmanager.npc.interactions.InteractEventType;
import io.github.scroojalix.npcmanager.npc.interactions.NPCInteractionData;
import io.github.scroojalix.npcmanager.npc.meta.NPCMetaInfo;
import io.github.scroojalix.npcmanager.npc.skin.SkinData;
import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

/**
 * Class that stores all of an NPC's traits.
 */
public class NPCTrait implements Serialisable {
    
    @Expose
    private String displayName;
    @Expose
    private String subtitle;
    @Expose
    private int range;
    @Expose
    private boolean headRotation;
    @Expose
    private SkinData skin;
    @Expose
    private NPCInteractionData interactEvent;
    @Expose
    private NPCEquipment equipment;
    @Expose
    private @Nonnull NPCMetaInfo metaInfo;
    
    NPCTrait() {
        //TODO do this for all fields & remove setters
        this.metaInfo = new NPCMetaInfo();
    }

    public NPCTrait(String displayName, int range, boolean headRotation) {
        this.displayName = displayName;
        this.range = range;
        this.headRotation = headRotation;
        this.metaInfo = new NPCMetaInfo();
    }

    /**
     * @return The display name of this NPC.
     */
    public String getDisplayName() {
        return this.displayName;
    }
    
    /**
     * Sets the display name of this NPC.
     * @param displayName The new display name.
     */
    public void setDisplayName(String displayName) {
        if (displayName == null) { this.displayName = null; }
        else { this.displayName = displayName.equalsIgnoreCase("null")?null:displayName; }
    }
    
    /**
     * @return The subtitle of this NPC.
     */
    public String getSubtitle() {
        return this.subtitle;
    }
    
    /**
     * Sets the subtitle of this NPC.
     * @param subtitle The new subtitle.
     */
    public void setSubtitle(String subtitle) {
        if (subtitle == null) { this.subtitle = null; }
        else { this.subtitle = subtitle.equalsIgnoreCase("null")?null:subtitle; }
    }
    
    /**
     * @return The distance at which this NPC will be visible from.
     */
    public int getRange() {
        return this.range;
    }
    
    /**
     * Sets this range at which this NPC will be visible from.
     * @param range The new range.
     */
    public void setRange(int range) {
        this.range = range;
    }
    
    /**
     * @return Whether or not this NPC has head rotation.
     */
    public boolean hasHeadRotation() {
        return this.headRotation;
    }
    
    /**
     * Sets whether or not this NPC has head rotation.
     * @param headRotation Should this NPC have head rotation or not?
     */
    public void setHeadRotation(boolean headRotation) {
        this.headRotation = headRotation;
    }

    public SkinData getSkinData() {
        return this.skin;
    }
    
    public void setSkinData(SkinData skinData) {
        this.skin = skinData;
    }
    
    /**
     * Gets the equipment that the NPC has, meaning armor, main hand and off hand items.
     * @return The NPC's equipment.
     */
    public NPCEquipment getEquipment(boolean modification) {
        if (equipment == null && modification)
            equipment = new NPCEquipment();
        return equipment;
    }

    /**
     * Remove an NPC's equipment
     */
    public void removeEquipment() {
        this.equipment = null;
    }

    /**
     * @return The interact event of this NPC.
     */
    public NPCInteractionData getInteractEvent() {
        return this.interactEvent;
    }

    /**
     * Sets the interact event of this NPC.
     * @param interactEvent The name of the interact event.
     */
    public void setInteractEvent(InteractEventType type, String value) {
        this.interactEvent = new NPCInteractionData(type, value);
    }

    public void removeInteractEvent() {
        this.interactEvent = null;
    }

    public @Nonnull NPCMetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void clearMetaInfo() {
        this.metaInfo = new NPCMetaInfo();
    }
}
