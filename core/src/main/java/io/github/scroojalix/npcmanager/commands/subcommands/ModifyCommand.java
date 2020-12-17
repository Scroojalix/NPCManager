package io.github.scroojalix.npcmanager.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.NPCTrait;

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
        return "/npc modify <npc> <key> [args...]";
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
            if (args[2].equalsIgnoreCase("equipment")) {
                if (sender instanceof Player) {
                    NPCData data = main.npc.getNPCs().get(args[1]);
                    Player p = (Player) sender;
                    p.openInventory(CommandUtils.getEquipmentInv(data));
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "Sorry console, but you can't do that.");
                    return true;
                }
            } else if (args[2].equalsIgnoreCase("interactEvent")) {
                NPCData data = main.npc.getNPCs().get(args[1]);
                if (args.length >= 5) {
                    if (args[3].equalsIgnoreCase("command")) {
                        String command = args[4];
                        for (int arg = 5; arg < args.length; arg++) {
                            command += " "+args[arg];
                        }
                        data.setInteractEvent(new CommandInteraction(command));
                        main.npc.saveNPC(data);
                        sender.sendMessage(PluginUtils.format("&6Set &F"+data.getName()+"'s &6Interact Event to the command &F/"+command));
                    } else if (args[3].equalsIgnoreCase("custom")) {
                        if (InteractionsManager.getInteractEvents().containsKey(args[4])) {
                            data.setInteractEvent(InteractionsManager.getInteractEvents().get(args[4]));
                            main.npc.saveNPC(data);
                            sender.sendMessage(PluginUtils.format("&6Set &F"+data.getName()+"'s &6Interact Event to &F"+args[3]));
                        } else {
                            sender.sendMessage(PluginUtils.format("&C'"+args[4]+"' is not a valid Interact Event."));
                        }
                    }
                    return true;
                } else if (args.length >= 4 && args[3].equalsIgnoreCase("none")) {
                    data.setInteractEvent(null);
                    main.npc.saveNPC(data);
                    sender.sendMessage(PluginUtils.format("&6Removed the Interact Event for &F"+data.getName()));
                    return true;
                }
            } else if (args.length >= 4) {
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
        }
        return false;
    }
    
}
