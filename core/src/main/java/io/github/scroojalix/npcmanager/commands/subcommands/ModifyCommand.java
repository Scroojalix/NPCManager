package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.NPCTrait;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class ModifyCommand extends SubCommand {

    @Override
    public String getName() {
        return "modify";
    }

    @Override
    public String getDescription() {
        return "Modifies an NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc modify <name> <key> <value>";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 3)
            return false;
        if (CommandUtils.npcExists(args[1], sender)) {
            NPCData modifying = main.npc.getNPCs().get(args[1]);
            NPCTrait traits = modifying.getTraits();
            String value = args[3];
            for (int arg = 4; arg < args.length; arg++) {
                value += " "+args[arg];
            }
            try {
                traits.modify(modifying, args[2], value);
            } catch(IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            } catch (Throwable t) {
                sender.sendMessage(PluginUtils.format(t.getMessage()));
            }
            main.npc.saveNPC(modifying);
            main.npc.updateNPC(modifying);
            return true;
        }
        return false;
    }
    
}
