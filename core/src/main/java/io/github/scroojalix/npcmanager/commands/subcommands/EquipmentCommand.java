package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class EquipmentCommand extends SubCommand {

    @Override
    public String getName() {    
        return "equipment";
    }

    @Override
    public String getDescription() {
        return "Change the equipment of an NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc equipment <npc>";
    }

    @Override
    public boolean consoleCanRun() {
        return false;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 2)
            return false;

        if (CommandUtils.npcExists(args[1], sender)) {
            NPCData data = main.npc.getNPCs().get(args[1]);
            Player p = (Player) sender;
            p.openInventory(CommandUtils.getEquipmentInv(data));
            return true;
        }
        return false;
    }
    
}
