package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class EquipmentModification extends SubCommand {

    public EquipmentModification() {
        super(
            "equipment",
            "Modify the equipment of an NPC.",
            "/npc modify <npc> equipment [--clear]",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        NPCData data = PluginUtils.getNPCDataByName(args[1]);
        if (args.length == 3) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.openInventory(CommandUtils.getEquipmentInv(data));
            } else {
                sender.sendMessage(ChatColor.RED + "Sorry console, but you can't do that.");   
            }
            return true;
        } else if (args[3].equalsIgnoreCase("--clear")) {
            data.getTraits().removeEquipment();
            main.npc.updateNPCPackets(data);
            sender.sendMessage(PluginUtils.format("&6Cleared &f%s's &6equipment.", data.getName()));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if (args.length == 4)
            result.add("--clear");
        return result;
    }

}
