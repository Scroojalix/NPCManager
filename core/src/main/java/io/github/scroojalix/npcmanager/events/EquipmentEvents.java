package io.github.scroojalix.npcmanager.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.utils.EmptySlots;
import io.github.scroojalix.npcmanager.utils.EquipmentInventory;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class EquipmentEvents implements Listener {

    private NPCMain main;
    
    public EquipmentEvents(NPCMain main) {
        this.main = main;
    }

    private void updateEquipment(NPCData data, ItemStack item, String slot) {
        switch(slot) {
        case "helmet":
            data.getTraits().getEquipment().setHelmet(item);
            break;
        case "chestplate":
            data.getTraits().getEquipment().setChestplate(item);
            break;
        case "leggings":
            data.getTraits().getEquipment().setLeggings(item);
            break;
        case "boots":
            data.getTraits().getEquipment().setBoots(item);
            break;
        case "mainhand":
            data.getTraits().getEquipment().setMainhandItem(item);
            break;
        case "offhand":
            data.getTraits().getEquipment().setOffhandItem(item);
            break;
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (e.getInventory().getHolder() instanceof EquipmentInventory) {
            NPCData data = main.npc.getNPCs().get(e.getView().getTitle().replace("'s Equipment", ""));
            if (e.getClickedInventory().equals(e.getView().getTopInventory())) {
                //Temp Variables
                Player p = (Player) e.getWhoClicked();
                ItemStack current = e.getCurrentItem();
                ItemStack cursor = e.getCursor();
                boolean notNull = cursor != null && cursor.getType() != Material.AIR;
                int[] slots = new int[] {10, 12, 14, 16, 30, 32};
                String[] types = new String[] {"helmet", "chestplate", "leggings", "boots", "mainhand", "offhand"};

                for (int i = 0; i < slots.length; i++) {
                    if (e.getRawSlot() == slots[i]) {
                        if (e.getRawSlot() == 32 && !NPCMain.serverVersion.hasOffHand) {
                            p.playSound(p.getLocation(), Sound.valueOf(CommandUtils.getErrorSound()), 5f, 0.5f);
                            p.sendMessage(ChatColor.RED+"That slot is disabled in this version of Minecraft!");
                            break;
                        }
                        if (notNull) {
                            if (PluginUtils.isSuitableItem(cursor, types[i], p)) {
                                e.getClickedInventory().setItem(slots[i], cursor);
                                if (current.isSimilar(EmptySlots.getArray().get(i))) {
                                    p.setItemOnCursor(null);
                                } else {
                                    p.setItemOnCursor(current);
                                }
                                p.updateInventory();
                                updateEquipment(data, cursor, types[i]);
                                main.npc.saveNPC(data); main.npc.updateNPC(data);
                            }
                        } else if (!current.isSimilar(EmptySlots.getArray().get(i))) {
                            e.getClickedInventory().setItem(slots[i], EmptySlots.getArray().get(i));
                            p.setItemOnCursor(current); p.updateInventory();
                            updateEquipment(data, null, types[i]);
                            main.npc.saveNPC(data); main.npc.updateNPC(data);
                        }
                        break;
                    }
                }
                e.setCancelled(true);
            }
        }
    }
}
