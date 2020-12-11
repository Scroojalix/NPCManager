package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

//TODO make this a subcommand of the modify command.
public class InteractionCommand extends SubCommand {

    @Override
    public String getName() {
        return "interactEvent";
    }

    @Override
    public String getDescription() {
        return "Customise what happens upon interaction.";
    }

    @Override
    public String getSyntax() {
        return "/npc interactEvent <npc> <type> <value>";
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
            NPCData data = main.npc.getNPCs().get(args[1]);
            if (args.length >= 4) {
                if (args[2].equalsIgnoreCase("command")) {
                    String command = args[3];
                    for (int arg = 4; arg < args.length; arg++) {
                        command += " "+args[arg];
                    }
                    data.setInteractEvent(new CommandInteraction(command));
                    main.npc.saveNPC(data);
                    sender.sendMessage(PluginUtils.format("&6Set &F"+data.getName()+"'s &6Interact Event to the command &F/"+command));
                } else if (args[2].equalsIgnoreCase("custom")) {
                    if (InteractionsManager.getInteractEvents().containsKey(args[3])) {
                        data.setInteractEvent(InteractionsManager.getInteractEvents().get(args[3]));
                        main.npc.saveNPC(data);
                        sender.sendMessage(PluginUtils.format("&6Set &F"+data.getName()+"'s &6Interact Event to &F"+args[3]));
                    } else {
                        sender.sendMessage(PluginUtils.format("&C'"+args[3]+"' is not a valid Interact Event."));
                    }
                }
                return true;
            } else if (args[2].equalsIgnoreCase("none")) {
                data.setInteractEvent(null);
                main.npc.saveNPC(data);
                sender.sendMessage(PluginUtils.format("&6Removed the Interact Event for &F"+data.getName()));
                return true;
            }
        }
        return false;
    }
    
}
