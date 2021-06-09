package io.github.scroojalix.npcmanager.utils.npc;

import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.utils.interactions.InteractEventType;
import io.github.scroojalix.npcmanager.utils.interactions.NPCInteractionData;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinData;
import io.github.scroojalix.npcmanager.utils.npc.equipment.NPCEquipment;
import io.github.scroojalix.npcmanager.utils.npc.skin.NPCSkinLayers;

/**
 * Class that stores all of an NPC's traits.
 */
public class NPCTrait {
    
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
    private NPCSkinLayers skinLayers;

    
    @Expose
    private NPCInteractionData interactEvent;
    @Expose
    private NPCEquipment equipment;
    
    public NPCTrait(String displayName, int range, boolean headRotation) {
        this.displayName = displayName;
        this.range = range;
        this.headRotation = headRotation;
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

    public NPCSkinLayers getSkinLayers() {
        return this.skinLayers;
    }

    public void setSkinLayers(NPCSkinLayers skinLayers) {
        this.skinLayers = skinLayers;
    }

    public byte getSkinLayersByte() {
        if (this.skinLayers != null) {
            return skinLayers.getDisplayedSkinParts();
        }
        return 127;
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
}
