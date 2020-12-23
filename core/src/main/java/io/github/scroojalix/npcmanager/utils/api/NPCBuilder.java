package io.github.scroojalix.npcmanager.utils.api;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

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

    //TODO redo this method
    public NPCBuilder setSkin(String skin) {
        if (NPCMain.instance.skinManager.values().contains(skin)) {
            data.getTraits().setSkin(skin);
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
