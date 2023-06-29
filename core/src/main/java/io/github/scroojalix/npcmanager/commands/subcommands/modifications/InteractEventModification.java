package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

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
            if (args[3].equalsIgnoreCase("command") && args.length > 4) {
                String command = args[4];
                for (int arg = 5; arg < args.length; arg++) {
                    command += " " + args[arg];
                }
                data.getTraits().setInteractEvent(InteractEventType.COMMAND, command);
                // TODO may not need to update the npc completely
                main.npc.hardResetNPC(data);
                sender.sendMessage(PluginUtils.format(
                        "&6Set &F" + data.getName() + "'s &6Interact Event to the command &F/" + command));
                return true;
            } else if (args[3].equalsIgnoreCase("custom") && args.length > 4) {
                if (InteractionsManager.getInteractEvents().containsKey(args[4])) {
                    data.getTraits().setInteractEvent(InteractEventType.CUSTOM, args[4]);
                    main.npc.hardResetNPC(data);
                    sender.sendMessage(PluginUtils
                            .format("&6Set &F" + data.getName() + "'s &6Interact Event to &F" + args[3]));
                } else {
                    sender.sendMessage(
                            PluginUtils.format("&C'" + args[4] + "' is not a valid Interact Event."));
                }
                return true;
            } else if (args[3].equalsIgnoreCase("none")) {
                data.getTraits().removeInteractEvent();
                main.npc.hardResetNPC(data);
                sender.sendMessage(PluginUtils.format("&6Removed the Interact Event for &F" + data.getName()));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if (args.length == 4) {
            result.add("command");
            result.add("custom");
            result.add("none");
        } else if (args.length == 5 && args[3].equalsIgnoreCase("custom")) {
            for (String interaction : InteractionsManager.getInteractEvents().keySet()) {
                result.add(interaction);
            }
        }
        return result;
    }

}
