package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.common.npc.NPCData;

public class EquipmentModification extends SubCommand {

    @Override
    public String getName() {
        return "equipment";
    }

    @Override
    public String getDescription() {
        return "Modify the equipment of an NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc modify <npc> equipment";
    }

    @Override
    public boolean consoleCanRun() {
        return false;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        NPCData data = main.npc.getNPCs().get(args[1]);
        Player p = (Player) sender;
        p.openInventory(CommandUtils.getEquipmentInv(data));
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        return new ArrayList<String>();
    }
    
}
