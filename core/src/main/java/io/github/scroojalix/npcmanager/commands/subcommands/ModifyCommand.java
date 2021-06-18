package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.commands.subcommands.modifications.*;
import io.github.scroojalix.npcmanager.common.PluginUtils;

public class ModifyCommand extends SubCommand {

    private ArrayList<SubCommand> subcommands = new ArrayList<SubCommand>();

    public ModifyCommand() {
        subcommands.add(new EquipmentModification());
        subcommands.add(new InteractEventModification());
        subcommands.add(new DisplayNameModification());
        subcommands.add(new SubtitleModification());
        subcommands.add(new HeadRotationModification());
        subcommands.add(new RangeModification());
        subcommands.add(new SkinModification());
        subcommands.add(new SkinLayersModification());
    }

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

    //TODO add ability to customise pose.
    //May have to allow multiple hologram lines first and calculate height of holograms dynamically.
    //Things to allow:
    //Sneaking
    //Crawling/swimming
    //Blocking (if using sword in old version, shield in new version)
    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length >= 3) {
            if (CommandUtils.npcExists(args[1], sender)) {
                for (SubCommand command : subcommands) {
                    if (args[2].equalsIgnoreCase(command.getName())) {
                        if (command.consoleCanRun() || sender instanceof Player) {
                            if (!command.execute(main, sender, args)) {
                                sender.sendMessage(ChatColor.RED + "Usage: " + command.getSyntax());
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Sorry console, but you can't do that.");
                        }
                        return true;
                    }
                } 
            } else {
                return false;
            }
        }
        for (SubCommand sub : subcommands) {
            sender.sendMessage(PluginUtils.format("&B" + sub.getSyntax() + " &F&L-&6 " + sub.getDescription()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if (args.length == 2) {
            return getNPCs(args[1]);
        } else if (args.length == 3) {
            for (SubCommand sub : subcommands) {
                result.add(sub.getName());
            }
        } else if (args.length >= 4) {
            for (SubCommand sub : subcommands) {
                if (args[2].equalsIgnoreCase(sub.getName())) {
                    result = sub.onTabComplete(args);
                    break;
                }
            }
        }
        return filter(args[args.length-1], result);
    }
    
}
