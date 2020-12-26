package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinLayers;

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
        NPCData data = main.npc.getNPCs().get(args[1]);
        SkinLayers layers = data.getTraits().getSkinLayers();
        if (args.length == 3 && layers != null) {
            sendCurrentConfiguration(sender, layers);
            return true;
        }
        LinkedHashMap<String, String> modifications = new LinkedHashMap<String, String>();
        for (int arg = 4; arg <= args.length; arg+=2) {
            String layer = args[arg-1];
            if (args.length >= arg + 1) {
                modifications.put(layer, args[arg]);
            }
        }
        if (!modifications.isEmpty()) {
            data.getTraits().setSkinLayers(applyModifications(sender, layers, modifications));
            main.npc.saveNPC(data);
            main.npc.updateNPC(data);
        } else {
            sender.sendMessage(ChatColor.RED+"No modifications were made, as the arguments were invalid.");
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        ArrayList<String> result = new ArrayList<String>();
        if (args.length % 2 == 0) {
            ArrayList<String> usedLayers = new ArrayList<String>();
            for (int arg = 3; arg < args.length; arg++) {
                for (String layer : SkinLayers.skinParts) {
                    if (args[arg].replace("--","").equalsIgnoreCase(layer))
                        usedLayers.add(layer);
                }
            }
            for (String layer : SkinLayers.skinParts) {
                if (!usedLayers.contains(layer)) result.add("--"+layer);
            }
        } else if (args.length <= 17){
            result.add("true"); result.add("false");
        }
        return result;
    }
    
    private void sendCurrentConfiguration(CommandSender sender, SkinLayers layers) {
        String[] skinParts = SkinLayers.skinParts;
        boolean[] values = layers.getBoolArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < skinParts.length; i++) {
            builder.append(ChatColor.GOLD+skinParts[i]+": "+ChatColor.WHITE+values[i]);
            if (i < skinParts.length - 1) builder.append(ChatColor.GOLD+", ");
        }
        sender.sendMessage(builder.toString());
    }

    private SkinLayers applyModifications(CommandSender sender, SkinLayers layers, LinkedHashMap<String, String> modifications) {
        if (layers == null) layers = new SkinLayers();
        for (Map.Entry<String, String> entry : modifications.entrySet()) {
            if (entry.getValue().equalsIgnoreCase("true") || entry.getValue().equalsIgnoreCase("false")) {
                boolean value = entry.getValue().equalsIgnoreCase("true");
                switch (entry.getKey()) {
                    case "--cape":
                        layers.setCape(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fcape &6to &f"+value));
                        break;
                    case "--jacket":
                        layers.setJacket(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fjacket &6to &f"+value));
                        break;
                    case "--leftSleeve":
                        layers.setLeftSleeve(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fleft sleeve &6to &f"+value));
                        break;
                    case "--rightSleeve":
                        layers.setRightSleeve(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fright sleeve &6to &f"+value));
                        break;
                    case "--leftLeg":
                        layers.setLeftLeg(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fleft leg &6to &f"+value));
                        break;
                    case "--rightLeg":
                        layers.setRightLeg(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fright leg &6to &f"+value));
                        break;
                    case "--hat":
                        layers.setHat(value);
                        sender.sendMessage(PluginUtils.format("&6Set the skin layer &fhat &6to &f"+value));
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED+"Invalid skin layer '"+entry.getKey()+"'");
                        break;
                }
            } else {
                sender.sendMessage(PluginUtils.format("&cCould not set the skin layer &f"
                    +entry.getKey().replace("--","")+" &cto the value &f"+entry.getValue()));
            }
        }
        return layers;
    }
}
