package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class MoveCommand extends SubCommand {

    public MoveCommand() {
        super(
            "move",
            "Moves an NPC to your current location.",
            "/npc move <npc> [x y z] [yaw pitch]",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 2)
            return false;
        String name = args[1];
        if (CommandUtils.npcExists(name, sender)) {
            NPCData data = PluginUtils.getNPCDataByName(name);
            if (args.length == 2) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED+"Sorry console, but you can't do that.");
                    return false;
                }
                main.npc.moveNPC(data, ((Player) sender).getLocation());
                sender.sendMessage(PluginUtils.format("&6Moved &F") + name + PluginUtils.format("&6 to your position."));
                return true;
            } else if (args.length == 5) {
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);

                    Location newLoc = new Location(data.getLoc().getWorld(), x, y, z);
                    main.npc.moveNPC(data, newLoc);
                    sender.sendMessage(PluginUtils.format(String.format(
                        "&6Moved &F%s to X=%g,Y=%g,Z=%g",
                        data.getName(),
                        x,y,z
                    )));
                    return true;
                } catch(NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Please use valid numbers.");
                    return false;
                }
            } else if (args.length == 7) {
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    float yaw = Float.parseFloat(args[5]);
                    float pitch = Float.parseFloat(args[6]);

                    Location newLoc = new Location(data.getLoc().getWorld(), x, y, z, yaw, pitch);
                    main.npc.moveNPC(data, newLoc);
                    sender.sendMessage(PluginUtils.format(String.format(
                        "&6Moved &F%s to X=%g,Y=%g,Z=%g,Yaw=%g,Pitch=%g",
                        data.getName(),
                        x,y,z,yaw,pitch
                    )));
                    return true;
                } catch(NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Please use valid numbers.");
                    return false;
                }
                
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if (args.length == 2) {
            return getNPCs(args[1]);
        } else {
            return new ArrayList<String>();
        }
    }

}
