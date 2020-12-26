package io.github.scroojalix.npcmanager.utils.api;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinManager;

public class NPCBuilder {

    private NPCData data;

    public NPCBuilder(String name, Location loc) {
        this.data = new NPCData(name, loc, true);
    }

    public NPCData create() {
        return data;
    }

    public NPCBuilder setInteractEvent(String type, String interaction) {
        if (type.equalsIgnoreCase("command")) {
            data.setInteractEvent(new CommandInteraction(interaction));
        } else if (type.equalsIgnoreCase("custom")) {
            if (InteractionsManager.getInteractEvents().containsKey(interaction)) {
                data.setInteractEvent(InteractionsManager.getInteractEvents().get(interaction));
            } else {
                NPCMain.instance.log(Level.WARNING, "Could not set the interact event of "+data.getName()+". The interact event '"+interaction+"' does not exist.");
            }
        } else {
            NPCMain.instance.log(Level.WARNING, "Could not set the interact event of "+data.getName()+". The type '"+type+"' is invalid.");
        }
        return this;
	}

    public NPCBuilder setDisplayName(String displayName) {
        data.getTraits().setDisplayName(displayName);
        return this;
    }
    
    public NPCBuilder setSubtitle(String subtitle) {
        data.getTraits().setSubtitle(subtitle);
        return this;
    }

    public NPCBuilder doNotStore() {
        data.setStored(false);
        return this;
    }

    public NPCBuilder removeHeadRotation() {
        data.getTraits().setHeadRotation(false);
        return this;
    }

    public NPCBuilder setRange(int range) {
        data.getTraits().setRange(range);
        return this;
    }

    //FIXME wait until NPC is spawned before updating skin.
    	/**
	 * Set the skin of an NPC.
	 * @param name The name of the NPC to modify.
	 * @param type The method of getting skin data. Can be set to <code>url</code> or <code>username</code>.
	 * @param value The URL to the skin image or the username to get textures from.
	 * @param optionalArg If <code>type</code> is set to <code>"url"</code> and
	 * this is set to <code>true</code>, then the resulting skin will use the slim model.
	 * Set it to <code>false</code> to use the default model. If <code>type</code> is set
	 * to <code>"username"</code>, then set this to <code>true</code> to automatically
	 * update the skin on every reload.
	 */
    public NPCBuilder setSkin(String type, String value, boolean optionalArg) {
        if (type.equalsIgnoreCase("url")) {
            SkinManager.setSkinFromURL(null, data, value, optionalArg);
        } else if (type.equalsIgnoreCase("username")) {
            SkinManager.setSkinFromUsername(null, data, value, optionalArg);
        }
        return this;
    }

    public NPCBuilder setHelmet(ItemStack helmet) {
        if (PluginUtils.isSuitableItem(helmet, "helmet", null)) {
            data.getTraits().getEquipment().setHelmet(helmet);
        }
        return this;
    }

    public NPCBuilder setChestplate(ItemStack chestplate) {
        if (PluginUtils.isSuitableItem(chestplate, "chestplate", null)) {
            data.getTraits().getEquipment().setChestplate(chestplate);
        }
        return this;
    }

    public NPCBuilder setLeggings(ItemStack leggings) {
        if (PluginUtils.isSuitableItem(leggings, "leggings", null)) {
            data.getTraits().getEquipment().setLeggings(leggings);
        }
        return this;
    }

    public NPCBuilder setBoots(ItemStack boots) {
        if (PluginUtils.isSuitableItem(boots, "boots", null)) {
            data.getTraits().getEquipment().setBoots(boots);
        }
        return this;
    }

    public NPCBuilder setMainhandItem(ItemStack mainhand) {
        if (PluginUtils.isSuitableItem(mainhand, "mainhand", null)) {
            data.getTraits().getEquipment().setMainhandItem(mainhand);
        }
        return this;
    }

    public NPCBuilder setOffhandItem(ItemStack offhand) {
        if (PluginUtils.isSuitableItem(offhand, "offhand", null)) {
            data.getTraits().getEquipment().setOffhandItem(offhand);
        }
        return this;
    }
}
