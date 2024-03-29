package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.skin.NPCSkinLayers;
import io.github.scroojalix.npcmanager.npc.skin.SkinLayer;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class SkinLayersModification extends SubCommand {

    @Override
    public String getName() {
        return "skinLayers";
    }

    @Override
    public String getDescription() {
        return "Customise the visible layers of an NPC's skin.";
    }

    @Override
    public String getSyntax() {
        return "/npc modify <npc> skinLayers [--layer <true|false>]...";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        NPCData data = PluginUtils.getNPCDataByName(args[1]);
        NPCSkinLayers layers = data.getTraits().getSkinLayers();
        if (args.length == 3) {
            sender.sendMessage((layers == null ? new NPCSkinLayers() : layers).getCurrentConfiguration());
            return true;
        }
        if (args[3].equalsIgnoreCase("--reset")) {
            data.getTraits().setSkinLayers(null);
            main.npc.updateNPC(data);
            sender.sendMessage(ChatColor.GOLD + "Reset skin layers for " + ChatColor.WHITE + data.getName());
            return true;
        }
        LinkedHashMap<String, String> modifications = new LinkedHashMap<String, String>();
        for (int arg = 4; arg <= args.length; arg += 2) {
            String layer = args[arg - 1];
            if (args.length >= arg + 1) {
                modifications.put(layer, args[arg]);
            }
        }
        if (!modifications.isEmpty()) {
            data.getTraits().setSkinLayers(applyModifications(sender, layers, modifications));
            main.npc.updateNPC(data);
        } else {
            sender.sendMessage(ChatColor.RED + "No modifications were made, as the arguments were invalid.");
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        ArrayList<String> result = new ArrayList<String>();
        if (args.length == 4) {
            result.add("--reset");
        }
        if (!args[3].equalsIgnoreCase("--reset")) {
            if (args.length % 2 == 0) {
                for (SkinLayer layer : SkinLayer.values()) {
                    result.add(layer.label);
                    for (int arg = 3; arg < args.length; arg++) {
                        if (args[arg].equalsIgnoreCase(layer.label)) {
                            result.remove(layer.label);
                            break;
                        }
                    }
                }
            } else if (args.length <= 17) {
                result.add("true");
                result.add("false");
            }
        }
        return result;
    }

    private NPCSkinLayers applyModifications(CommandSender sender, NPCSkinLayers layers,
            LinkedHashMap<String, String> modifications) {
        if (layers == null)
            layers = new NPCSkinLayers();
        for (Map.Entry<String, String> entry : modifications.entrySet()) {
            if (entry.getValue().equalsIgnoreCase("true") || entry.getValue().equalsIgnoreCase("false")) {
                boolean value = entry.getValue().equalsIgnoreCase("true");
                switch (entry.getKey()) {
                    case "--cape":
                        layers.setCape(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fcape &6to &f" + value));
                        break;
                    case "--jacket":
                        layers.setJacket(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fjacket &6to &f" + value));
                        break;
                    case "--leftSleeve":
                        layers.setLeftSleeve(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fleft sleeve &6to &f" + value));
                        break;
                    case "--rightSleeve":
                        layers.setRightSleeve(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fright sleeve &6to &f" + value));
                        break;
                    case "--leftLeg":
                        layers.setLeftLeg(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fleft leg &6to &f" + value));
                        break;
                    case "--rightLeg":
                        layers.setRightLeg(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fright leg &6to &f" + value));
                        break;
                    case "--hat":
                        layers.setHat(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fhat &6to &f" + value));
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid skin layer '" + entry.getKey() + "'");
                        break;
                }
            } else {
                sender.sendMessage(PluginUtils.format("&cCould not set the skin layer &f"
                        + entry.getKey().replace("--", "") + " &cto the value &f" + entry.getValue()));
            }
        }
        return layers;
    }
}
