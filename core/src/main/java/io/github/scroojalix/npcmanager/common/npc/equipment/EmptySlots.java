package io.github.scroojalix.npcmanager.common.npc.equipment;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.PluginUtils;

public class EmptySlots {

    public static ItemStack helmetSlot;
    public static ItemStack chestplateSlot;
    public static ItemStack leggingsSlot;
    public static ItemStack bootsSlot;
    public static ItemStack mainhandSlot;
    public static ItemStack offhandSlot;
    public static ItemStack fillerItem;
    
    public static List<ItemStack> getArray() {
        List<ItemStack> array = new ArrayList<ItemStack>();
        array.add(helmetSlot);
        array.add(chestplateSlot);
        array.add(leggingsSlot);
        array.add(bootsSlot);
        array.add(mainhandSlot);
        array.add(offhandSlot);
        return array;
    }

    @SuppressWarnings("deprecation")
    private static ItemStack getFillerItem(String name, boolean itemSlotFiller) {
        ItemStack emptySlot;
        if (itemSlotFiller) {
            if (!name.equalsIgnoreCase("Off Hand") || NPCMain.serverVersion.hasOffHand) {
                if (NPCMain.serverVersion.usesDamageForColouredMaterials) {
                    emptySlot = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"));
                    emptySlot.setDurability((short) 5);
                } else {
                    emptySlot = new ItemStack(Material.LIME_STAINED_GLASS_PANE);   
                }
                ItemMeta tempMeta = emptySlot.getItemMeta();
                tempMeta.setDisplayName(itemSlotFiller?PluginUtils.format("&a&n&L"+name):name);
                List<String> lore = new ArrayList<String>();
                lore.add(PluginUtils.format("&7Place a suitable item here"));
                lore.add(PluginUtils.format("&7to equip the NPC with it."));
                tempMeta.setLore(lore);
                emptySlot.setItemMeta(tempMeta);
            } else {
                emptySlot = new ItemStack(Material.BARRIER);
                ItemMeta tempMeta = emptySlot.getItemMeta();
                tempMeta.setDisplayName(PluginUtils.format("&c&n&L"+name));
                List<String> lore = new ArrayList<String>();
                lore.add(PluginUtils.format("&7This slot is disabled in"));
                lore.add(PluginUtils.format("&7this version of Minecraft!"));
                tempMeta.setLore(lore);
                emptySlot.setItemMeta(tempMeta);
            }
        } else {
            if (NPCMain.serverVersion.usesDamageForColouredMaterials) {
                emptySlot = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"));
                emptySlot.setDurability((short) 7);
            } else {
                emptySlot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            }
            ItemMeta fillerMeta = emptySlot.getItemMeta();
            fillerMeta.setDisplayName(" ");
            fillerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            emptySlot.setItemMeta(fillerMeta);
        }
		return emptySlot;
    }

    public static void generateItems() {
        helmetSlot = getFillerItem("Helmet", true);
        chestplateSlot = getFillerItem("Chestplate", true);
        leggingsSlot = getFillerItem("Leggings", true);
        bootsSlot = getFillerItem("Boots", true);
        mainhandSlot = getFillerItem("Main Hand", true);
        offhandSlot = getFillerItem("Off Hand", true);

		fillerItem = getFillerItem("", false);
    }
}
