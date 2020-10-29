package me.scroojalix.npcmanager.utils;

import com.google.gson.annotations.Expose;

import org.bukkit.ChatColor;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.api.InteractionsManager;

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

    public void modify(NPCData data, String key, String value) throws IllegalArgumentException {
    switch(key) {
        case "displayName":
            setDisplayName(ChatColor.stripColor(NPCMain.instance.format(value)).isEmpty()?null:value);
            NPCMain.instance.npc.updateNPC(data);
            break;
        case "hasHeadRotation":
            setHeadRotation(value.equalsIgnoreCase("true"));
            NPCMain.instance.npc.updateNPC(data);
            break;
        case "range":
            try {
                Integer range = Integer.parseInt(value);
                if (range <= 0) {
                    throw new IllegalArgumentException("Range cannot be set to 0");
                }
                setRange(range);
                NPCMain.instance.npc.updateNPC(data);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("'"+value+"' is not a number.");
            }
            break;
        case "skin":
            NPCMain.instance.npc.setSkin(data, value);
            break;
        case "interactEvent":
            if (!value.equalsIgnoreCase("None")) {
                if (InteractionsManager.getInteractEvents().containsKey(value)) {
                    data.setInteractEvent(InteractionsManager.getInteractEvents().get(value));
                    NPCMain.instance.npc.updateNPC(data);
                } else {
                    throw new IllegalArgumentException("'"+value+"' is not a valid Interact Event.");
                }
            } else {
                data.setInteractEvent(null);
                NPCMain.instance.npc.updateNPC(data);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown key '"+key+"'.");
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSubtitle() {
        return this.subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public boolean hasHeadRotation() {
        return this.headRotation;
    }

    public void setHeadRotation(boolean headRotation) {
        this.headRotation = headRotation;
    }

    public String getSkin() {
        return this.skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getInteractEvent() {
        return this.interactEvent;
    }

    public void setInteractEvent(String interactEvent) {
        this.interactEvent = interactEvent;
    }
}
