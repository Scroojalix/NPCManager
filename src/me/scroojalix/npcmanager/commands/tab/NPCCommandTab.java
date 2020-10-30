package me.scroojalix.npcmanager.commands.tab;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.api.InteractionsManager;

public class NPCCommandTab implements TabCompleter {
	
	private NPCMain main;
	private List<String> arg0 = new ArrayList<String>();
	
	public NPCCommandTab(NPCMain main) {
		this.main = main;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (arg0.isEmpty()) {
			arg0.add("help"); arg0.add("list"); arg0.add("clear");
			arg0.add("create"); arg0.add("modify");
			arg0.add("move"); arg0.add("remove");
			arg0.add("reload"); arg0.add("info");
		}
		List<String> result = new ArrayList<String>();
		switch (args.length) {
		case 1:
			for (String a : arg0) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(a);
				}
			}
			return result;
		case 2:
			result.clear();
			if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("modify") || args[0].equalsIgnoreCase("move") || args[0].equalsIgnoreCase("info")) {
				for (String npc : main.npc.getNPCs().keySet()) {
					if (npc.toLowerCase().startsWith(args[1].toLowerCase())) result.add(npc);
				}
				return result;
			}
			break;
		case 3:
			result.clear();
			if (args[0].equalsIgnoreCase("modify")) {
				List<String> arg2 = new ArrayList<String>();
				arg2.add("displayName"); arg2.add("subtitle"); 
				arg2.add("hasHeadRotation"); arg2.add("range"); 
				arg2.add("skin"); arg2.add("interactEvent");
				for (String a : arg2) {
					if (a.toLowerCase().startsWith(args[2].toLowerCase())) result.add(a);
				}
				return result;
			}
			break;
		case 4: 
			result.clear();
			if (args[0].equalsIgnoreCase("modify")) {
				List<String> arg3 = new ArrayList<String>();
				if (args[2].equalsIgnoreCase("displayName") || args[2].equalsIgnoreCase("subtitle")) {
					arg3.add("null");
					for (String a : arg3) {
						if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
					}
					return result;
				}
				else if (args[2].equalsIgnoreCase("hasHeadRotation")) {
					arg3.add("true"); arg3.add("false");
					for (String a : arg3) {
						if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
					}
					return result;
				}
				else if (args[2].equalsIgnoreCase("skin")) {
					for (String skin : main.skinManager.values()) {
						arg3.add(skin);
					}
					arg3.add("Default");
					for (String a : arg3) {
						if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
					}
					return result;
				}
				else if (args[2].equalsIgnoreCase("interactEvent")) {
					for (String interaction : InteractionsManager.getInteractEvents().keySet()) {
						arg3.add(interaction);
					}
					arg3.add("None");
					for (String a : arg3) {
						if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
					}
					return result;
				}
			}
			break;
		}
		return new ArrayList<String>();
	}

}
