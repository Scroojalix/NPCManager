package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.interactions.InteractEventType;
import io.github.scroojalix.npcmanager.npc.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class InteractEventModification extends SubCommand {

    //TODO upgrade command interactions.
    //option to execute command as console
    //add variables eg ${playername}

    public InteractEventModification() {
        super(
            "interactEvent",
            "Customise the NPC's interact event.",
            "/npc modify <npc> interactEvent <command|custom|none> [args...]",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        NPCData data = PluginUtils.getNPCDataByName(args[1]);
        if (args.length > 3) {
            // Parse Interact Event Type
            InteractEventType type = null;
            for (InteractEventType interactEventType : InteractEventType.values()) {
                if (args[3].equalsIgnoreCase(interactEventType.commandString)) {
                    type = interactEventType;
                }
            }
            if (type == null) {
                sender.sendMessage(ChatColor.RED + "That is not a valid interact event type.");
                return false;
            }

            if (type.equals(InteractEventType.NONE)) {
                data.getTraits().removeInteractEvent();
                main.npc.hardResetNPC(data);
                sender.sendMessage(PluginUtils.format("&6Removed the Interact Event for &F" + data.getName()));
                return true;
            }

            if (args.length == 4) return false;

            if (type.equals(InteractEventType.CONSOLE_COMMAND) || type.equals(InteractEventType.PLAYER_COMMAND)) {
                // Compile command
                // TODO do this better, maybe get player to run command after wards.
                String command = args[4];
                for (int arg = 5; arg < args.length; arg++) {
                    command += " " + args[arg];
                }

                data.getTraits().setInteractEvent(type, command);
                main.npc.hardResetNPC(data);
                sender.sendMessage(
                    PluginUtils.format("&F%s's &6Interact Event set to %s &F/%s",
                    data.getName(), type.infoPrefix, command));
            } else {
                if (InteractionsManager.getInteractEvents().containsKey(args[4])) {
                    data.getTraits().setInteractEvent(type, args[4]);
                    main.npc.hardResetNPC(data);
                    sender.sendMessage(
                        PluginUtils.format("&F%s's &6Interact Event set to %s &F%s",
                        data.getName(), type.infoPrefix, args[4]));
                } else {
                    sender.sendMessage(
                        PluginUtils.format("&C'%s' is not a valid Interact Event.", args[4]));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if (args.length == 4) {
            for (InteractEventType type : InteractEventType.values()) {
                result.add(type.commandString);
            }
        } else if (args.length == 5 && args[3].equalsIgnoreCase("custom")) {
            for (String interaction : InteractionsManager.getInteractEvents().keySet()) {
                result.add(interaction);
            }
        }
        return result;
    }

}
