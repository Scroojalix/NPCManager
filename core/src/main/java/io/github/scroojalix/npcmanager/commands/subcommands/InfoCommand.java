package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.interactions.NPCInteractionData;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.NPCTrait;
import io.github.scroojalix.npcmanager.utils.npc.skin.NPCSkinLayers;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinData;

public class InfoCommand extends SubCommand {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Provides info on an NPC.";
    }

    @Override
    public String getSyntax() {
        return "/npc info <npc> [page]";
    }

    @Override
    public boolean consoleCanRun() {
        return true;
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (args.length < 2)
        return false;
        if (CommandUtils.npcExists(args[1], sender)) {
            NPCData data = main.npc.getNPCs().get(args[1]);
            NPCTrait traits = data.getTraits();
            if (args.length == 2 || args[2].equalsIgnoreCase("1")) {
                CommandUtils.sendJSONMessage(sender, CommandUtils.getTitleMessage("NPC Info"));
                sender.sendMessage(PluginUtils.format("&6Name: &F" + data.getName()));
                CommandUtils.sendJSONMessage(sender, CommandUtils.getLocationComponents(data.getName(), data.getLoc()));
                sender.sendMessage(PluginUtils.format("&6Display Name: &F" + traits.getDisplayName()));
                sender.sendMessage(PluginUtils.format("&6Subtitle: &F" + traits.getSubtitle()));
                sender.sendMessage(PluginUtils.format("&6Range: &F" + traits.getRange()));
                sender.sendMessage(PluginUtils.format("&6Head Rotation: &F" + traits.hasHeadRotation()));
                CommandUtils.sendJSONMessage(sender, CommandUtils.getEquipmentComponents(data.getName()));
                if (data.getInteractEvent() != null) {
                    NPCInteractionData interactEvent = traits.getInteractEvent();
                    sender.sendMessage(PluginUtils.format("&6Interact Event: "+interactEvent.getType().infoPrefix
                        +traits.getInteractEvent().getValue()));
                } else {
                    sender.sendMessage(PluginUtils.format("&6Interact Event: &fnull"));
                }
                CommandUtils.sendJSONMessage(sender, CommandUtils.getPageTurnerMessage("/npc info " + data.getName(), 2, 1));
            } else if (args[2].equalsIgnoreCase("2")) {
                CommandUtils.sendJSONMessage(sender, CommandUtils.getTitleMessage("NPC Info"));
                SkinData skinData = traits.getSkinData();
                sender.sendMessage(PluginUtils.format("&6Skin: &F"+(skinData==null?null:skinData.getSkinName())));
                NPCSkinLayers layers = traits.getSkinLayers();
                sender.sendMessage(ChatColor.GOLD+"Skin Layers: "+(layers==null?new NPCSkinLayers():layers).getCurrentConfiguration());
                if (sender instanceof Player) {
                    sender.sendMessage("");
                    sender.sendMessage("");
                    sender.sendMessage("");
                    sender.sendMessage("");
                    sender.sendMessage("");
                }
                CommandUtils.sendJSONMessage(sender, CommandUtils.getPageTurnerMessage("/npc info " + data.getName(), 2, 2));
            } else {
                sender.sendMessage(ChatColor.RED + "That is not a valid page number.");
            }
            return true;
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
