package io.github.scroojalix.npcmanager.commands.subcommands.modifications;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.skin.SkinManager;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class SkinModification extends SubCommand {

    @Override
    public String getName() {
        return "skin";
    }

    @Override
    public String getDescription() {
        return "Customise the NPC's skin.";
    }

    @Override
    public String getSyntax() {
        return "/npc modify <npc> skin <type> [value] [options]";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length >= 4) {
            NPCData data = PluginUtils.getNPCDataByName(args[1]);
            if (args[3].equalsIgnoreCase("url")) {
                if (args.length > 4) {
                    sender.sendMessage(PluginUtils.format("&6Attempting to create skin data from the url &F"
                            + args[4] + "&6. This may take a while"));
                    boolean slimModel = false;
                    if (args.length > 5) {
                        for (int arg = 5; arg < args.length; arg++) {
                            if (args[arg].equalsIgnoreCase("--slimModel")) {
                                slimModel = true;
                                break;
                            }
                        }
                    }
                    data.getTraits().setSkinLayers(null);
                    SkinManager.setSkinFromURL(sender, data, args[4], slimModel);
                    return true;
                }
            } else if (args[3].equalsIgnoreCase("username")) {
                if (args.length > 4) {
                    if (PluginUtils.isAlphanumeric(args[4]) && args[4].length() <= 16) {
                        sender.sendMessage(PluginUtils.format("&6Attempting to get skin data from the username &F"
                                + args[4] + "&6. This may take a while"));
                        boolean keepLatest = false;
                        if (args.length > 5) {
                            for (int arg = 5; arg < args.length; arg++) {
                                if (args[arg].equalsIgnoreCase("--keepLatest")) {
                                    keepLatest = true;
                                    break;
                                }
                            }
                        }
                        data.getTraits().setSkinLayers(null);
                        SkinManager.setSkinFromUsername(sender, data, args[4], keepLatest);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "That username is not valid.");
                    }
                }
            } else if (args[3].equalsIgnoreCase("default")) {
                data.getTraits().setSkinData(null);
                data.getTraits().setSkinLayers(null);
                main.storage.saveNPC(data);
                main.npc.updateNPC(data);
                sender.sendMessage(PluginUtils.format("&6Reset the skin of &F" + data.getName()));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> result = new ArrayList<String>();
        if (args.length == 4) {
            result.add("url");
            result.add("username");
            result.add("default");
        } else if (args.length > 5) {
            if (args[3].equalsIgnoreCase("url")) {
                result.add("--slimModel");
            } else if (args[3].equalsIgnoreCase("username")) {
                result.add("--keepLatest");
            }
        }
        return result;
    }

}