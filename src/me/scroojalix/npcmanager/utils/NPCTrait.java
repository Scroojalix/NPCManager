package me.scroojalix.npcmanager.utils;

import com.google.gson.annotations.Expose;

import me.scroojalix.npcmanager.NPCMain;

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
    private String skin;
    @Expose
    private String interactEvent;

    public NPCTrait(String displayName, String subtitle, int range, boolean headRotation) {
        this.displayName = displayName;
        this.subtitle = subtitle;
        this.range = range;
        this.headRotation = headRotation;
    }

    /**
     * Modify an NPC's traits.
     * @param data The NPC to be modified.
     * @param key The modification to be made.
     * @param value The new value.
     * @throws IllegalArgumentException If any of the arguments are not valid.
     * @throws Throwable When a modification is successful, contains a message for player.
     */
    public void modify(NPCData data, String key, String value) throws IllegalArgumentException, Throwable {
    switch(key) {
        case "displayName":
            setDisplayName(value.equalsIgnoreCase("null")?null:value);
            throw new Throwable("&6Set the display name of &F"+data.getName()+"&6 to &F"+value);
        case "subtitle":
            setSubtitle(value.equalsIgnoreCase("null")?null:value);
            throw new Throwable("&6Set the subtitle of &F"+data.getName()+"&6 to &F"+value);
        case "hasHeadRotation":
            setHeadRotation(value.equalsIgnoreCase("true"));
            throw new Throwable("&6Set the head rotation of &F"+data.getName()+"&6 to &F"+headRotation);
        case "range":
            try {
                Integer range = Integer.parseInt(value);
                if (range <= 0) {
                    throw new IllegalArgumentException("Range cannot be set to 0");
                }
                setRange(range);
                throw new Throwable("&6Set the range of &F"+data.getName()+"&6 to &F"+range);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("'"+value+"' is not a number.");
            }
        case "skin":
            if (value.equalsIgnoreCase("Default") || NPCMain.instance.skinManager.values().contains(value)) {
                data.getTraits().setSkin(value);
                throw new Throwable("&6Set the skin of &F"+data.getName()+"&6 to &F"+value);
            } else {
                throw new IllegalArgumentException("'"+value+"' is not a valid skin.");
            }
        case "interactEvent":
            if (InteractionsManager.getInteractEvents().containsKey(value) || value.equalsIgnoreCase("None")) {
                data.setInteractEvent(value.equalsIgnoreCase("None")?null:InteractionsManager.getInteractEvents().get(value));
                throw new Throwable("&6Set the interact event of &F"+data.getName()+"&6 to &F"+value);
            } else {
                throw new IllegalArgumentException("'"+value+"' is not a valid Interact Event.");
            }
        default:
            throw new IllegalArgumentException("Unknown key '"+key+"'.");
        }
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
        this.displayName = displayName;
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
        this.subtitle = subtitle;
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

    /**
     * Returns the name of this NPC's skin.
     * <p>
     * The skins are configured in the skins.yml file in the plugin folder.
     * @return The name of this NPC's skin.
     */
    public String getSkin() {
        return this.skin;
    }

    /**
     * Sets the skin of this NPC.
     * @param skin The name of the new skin for the NPC, defined in skins.yml.
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }

    /**
     * @return The interact event of this NPC.
     */
    public String getInteractEvent() {
        return this.interactEvent;
    }

    /**
     * Sets the interact event of this NPC.
     * @param interactEvent The name of the interact event.
     */
    public void setInteractEvent(String interactEvent) {
        this.interactEvent = interactEvent;
    }
}
