package io.github.scroojalix.npcmanager.commands.subcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.commands.SubCommand;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class ListCommand extends SubCommand {

    public ListCommand() {
        super(
            "list",
            "Prints a detailed list of all NPC's.",
            "/npc list [page]",
            true
        );
    }

    @Override
    public boolean execute(NPCMain main, CommandSender sender, String[] args) {
        if (!PluginUtils.noNPCs()) {
            //Calculate Number of Pages.
            int size = PluginUtils.getNumberOfNPCs();
            int remainder = size % 8;
            int pages = ((size - remainder) / 8) + (remainder != 0?1:0);

            //Get Input Page
            int page;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                    if (page > pages) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED+"Not a valid page number '"+args[1]+"'");
                    return true;
                }
            } else {
                page = 1;
            }

            //Print Correct NPCs
            CommandUtils.sendJSONMessage(sender, CommandUtils.getTitleMessage("NPC List"));
            List<String> keys = new ArrayList<String>(PluginUtils.getAllNPCNames());
            for (int i = 0; i < 8; i++) {
                int npcIndex = ((page-1)*8)+i;
                try {
                    String npc = keys.get(npcIndex);
                    CommandUtils.sendJSONMessage(sender, CommandUtils.getListComponents(npc));
                } catch (IndexOutOfBoundsException e) {
                    sender.sendMessage("");
                }
            }
            CommandUtils.sendJSONMessage(sender, CommandUtils.getPageTurnerMessage("/npc list", pages, page));
        } else {
            sender.sendMessage(ChatColor.RED+Messages.NO_NPCS);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        return new ArrayList<String>();
    }
    
}
