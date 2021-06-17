package io.github.scroojalix.npcmanager.common.npc.equipment;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Useful for checking if an interacted inventory is from this plugin.
 */
public class EquipmentInventory implements InventoryHolder {

    @Override
    public Inventory getInventory() {
        return null;
    }
    
}