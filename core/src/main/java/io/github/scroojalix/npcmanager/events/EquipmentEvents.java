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

    //TODO prevent placing item stack with amount higher than 1.
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

                //TODO convert this to a loop to reduce file size
                
                switch (e.getRawSlot()) {
                case 10: //Helmet
                if (notNull) {
                    if (PluginUtils.isSuitableItem(cursor, "helmet", p)) {
                        e.getClickedInventory().setItem(10, cursor);
                        if (current.isSimilar(EmptySlots.helmetSlot)) {
                            p.setItemOnCursor(null);
                        } else {
                            p.setItemOnCursor(current);
                        }
                        p.updateInventory();
                        data.getTraits().getEquipment().setHelmet(cursor);
                        main.npc.saveNPC(data); main.npc.updateNPC(data);
                    }
                } else if (!current.isSimilar(EmptySlots.helmetSlot)) {
                    e.getClickedInventory().setItem(10, EmptySlots.helmetSlot);
                    p.setItemOnCursor(current); p.updateInventory();
                    data.getTraits().getEquipment().setHelmet(null);
                    main.npc.saveNPC(data); main.npc.updateNPC(data);
                }
                break;
                case 12: //Chestplate
                if (notNull) {
                    if (PluginUtils.isSuitableItem(cursor, "chestplate", p)) {
                        e.getClickedInventory().setItem(12, cursor);
                        if (current.isSimilar(EmptySlots.chestplateSlot)) {
                            p.setItemOnCursor(null);
                        } else {
                            p.setItemOnCursor(current);
                        }
                        p.updateInventory();
                        data.getTraits().getEquipment().setChestplate(cursor);
                        main.npc.saveNPC(data); main.npc.updateNPC(data);
                    }
                } else if (!current.isSimilar(EmptySlots.chestplateSlot)) {
                    e.getClickedInventory().setItem(12, EmptySlots.chestplateSlot);
                    p.setItemOnCursor(current); p.updateInventory();
                    data.getTraits().getEquipment().setChestplate(null);
                    main.npc.saveNPC(data); main.npc.updateNPC(data);
                }
                break;
                case 14: //Leggings
                if (notNull) {
                    if (PluginUtils.isSuitableItem(cursor, "leggings", p)) {
                        e.getClickedInventory().setItem(14, cursor);
                        if (current.isSimilar(EmptySlots.leggingsSlot)) {
                            p.setItemOnCursor(null);
                        } else {
                            p.setItemOnCursor(current);
                        }
                        p.updateInventory();
                        data.getTraits().getEquipment().setLeggings(cursor);
                        main.npc.saveNPC(data); main.npc.updateNPC(data);
                    }
                } else if (!current.isSimilar(EmptySlots.leggingsSlot)) {
                    e.getClickedInventory().setItem(14, EmptySlots.leggingsSlot);
                    p.setItemOnCursor(current); p.updateInventory();
                    data.getTraits().getEquipment().setLeggings(null);
                    main.npc.saveNPC(data); main.npc.saveNPC(data); main.npc.updateNPC(data);
                }
                break;
                case 16: //Boots
                if (notNull) {
                    if (PluginUtils.isSuitableItem(cursor, "boots", p)) {
                        e.getClickedInventory().setItem(16, cursor);
                        if (current.isSimilar(EmptySlots.bootsSlot)) {
                            p.setItemOnCursor(null);
                        } else {
                            p.setItemOnCursor(current);
                        }
                        p.updateInventory();
                        data.getTraits().getEquipment().setBoots(cursor);
                        main.npc.saveNPC(data); main.npc.updateNPC(data);
                    }
                } else if (!current.isSimilar(EmptySlots.bootsSlot)) {
                    e.getClickedInventory().setItem(16, EmptySlots.bootsSlot);
                    p.setItemOnCursor(current); p.updateInventory();
                    data.getTraits().getEquipment().setBoots(null);
                    main.npc.saveNPC(data); main.npc.updateNPC(data);
                }
                break;
                case 30: //Main Hand
                if (notNull) {
                    if (PluginUtils.isSuitableItem(cursor, "item", p)) {
                        e.getClickedInventory().setItem(30, cursor);
                        if (current.isSimilar(EmptySlots.mainhandSlot)) {
                            p.setItemOnCursor(null);
                        } else {
                            p.setItemOnCursor(current);
                        }
                        p.updateInventory();
                        data.getTraits().getEquipment().setMainhandItem(cursor);
                        main.npc.saveNPC(data); main.npc.updateNPC(data);
                    }
                } else if (!current.isSimilar(EmptySlots.mainhandSlot)) {
                    e.getClickedInventory().setItem(30, EmptySlots.mainhandSlot);
                    p.setItemOnCursor(current); p.updateInventory();
                    data.getTraits().getEquipment().setMainhandItem(null);
                    main.npc.saveNPC(data); main.npc.updateNPC(data);
                }
                break;
                case 32: //Off Hand
                if (NPCMain.serverVersion.hasOffHand) {
                    if (notNull) {
                        if (PluginUtils.isSuitableItem(cursor, "item", p)) {
                            e.getClickedInventory().setItem(32, cursor);
                            if (current.isSimilar(EmptySlots.offhandSlot)) {
                                p.setItemOnCursor(null);
                            } else {
                                p.setItemOnCursor(current);
                            }
                            p.updateInventory();
                            data.getTraits().getEquipment().setOffhandItem(cursor);
                            main.npc.saveNPC(data); main.npc.updateNPC(data);
                        }
                    } else if (!current.isSimilar(EmptySlots.offhandSlot)) {
                        e.getClickedInventory().setItem(32, EmptySlots.offhandSlot);
                        p.setItemOnCursor(current); p.updateInventory();
                        data.getTraits().getEquipment().setOffhandItem(null);
                        main.npc.saveNPC(data); main.npc.updateNPC(data);
                    }
                } else {
                    p.playSound(p.getLocation(), Sound.valueOf(CommandUtils.getErrorSound()), 5f, 0.5f);
                    p.sendMessage(ChatColor.RED+"That slot is disabled in this version of Minecraft!");
                }
                break;
                }
                e.setCancelled(true);
            }
        }
    }
}
