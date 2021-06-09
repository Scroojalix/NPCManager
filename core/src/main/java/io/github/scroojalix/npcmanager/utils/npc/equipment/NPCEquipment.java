package io.github.scroojalix.npcmanager.utils.npc.equipment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.utils.storage.misc.Serialisable;

public class NPCEquipment implements Serialisable {
    
    @Expose
    private ItemStack helmet;
    @Expose
    private ItemStack chestplate;
    @Expose
    private ItemStack leggings;
    @Expose
    private ItemStack boots;
    @Expose
    private ItemStack mainhand;
    @Expose
    private ItemStack offhand;

    public List<ItemStack> getEquipmentArray() {
        List<ItemStack> equipment = new ArrayList<ItemStack>();
        equipment.add(helmet);
        equipment.add(chestplate);
        equipment.add(leggings);
        equipment.add(boots);
        equipment.add(mainhand);
        equipment.add(offhand);
        return equipment;
    }

    public ItemStack getMainhandItem() {
        return this.mainhand;
    }

    public void setMainhandItem(ItemStack mainhand) {
        this.mainhand = mainhand;
    }

    public ItemStack getOffhandItem() {
        return this.offhand;
    }

    public void setOffhandItem(ItemStack offhand) {
        this.offhand = offhand;
    }

    public ItemStack getHelmet() {
        return this.helmet;
    }

    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }

    public ItemStack getChestplate() {
        return this.chestplate;
    }

    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    public ItemStack getLeggings() {
        return this.leggings;
    }

    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
    }

    public ItemStack getBoots() {
        return this.boots;
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }    
}
