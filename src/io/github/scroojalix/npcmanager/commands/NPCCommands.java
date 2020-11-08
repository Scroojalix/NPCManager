package io.github.scroojalix.npcmanager.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.tab.NPCCommandTab;
import io.github.scroojalix.npcmanager.utils.NPCData;
import io.github.scroojalix.npcmanager.utils.NPCTrait;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class NPCCommands implements CommandExecutor {

	private NPCMain main;
	
	public NPCCommands(NPCMain main) {
		this.main = main;
		main.getCommand("npc").setTabCompleter(new NPCCommandTab(main));
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("npc")) {
			if (args.length == 0) return false;
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(main.format(" &6&M&L                       &b NPC Help &6&M&L                       &r "));
				sender.sendMessage(main.format("&B/npc create <name> &F&L-&6 Creates an NPC with name <name>."));
				sender.sendMessage(main.format("&B/npc remove <name> &F&L-&6 Removes an NPC with name <name>."));
				sender.sendMessage(main.format("&B/npc modify <name> <key> <value> &F&L-&6 Modifies an NPC."));
				sender.sendMessage(main.format("&B/npc info <name> &F&L-&6 Displays info on an NPC."));
				sender.sendMessage(main.format("&B/npc move <name> &F&L-&6 Moves an NPC to your position."));
				sender.sendMessage(main.format("&B/npc list &F&L-&6 Lists all NPC's."));
				sender.sendMessage(main.format("&B/npc clear &F&L-&6 Clears all NPC's."));
				sender.sendMessage(main.format("&B/npc reload &F&L-&6 Reloads the plugin."));
				return true;
			}
			else if (args[0].equalsIgnoreCase("info")) {
				if (args.length == 2) {
					if (main.npc.getNPCs().containsKey(args[1])) {
						NPCData data = main.npc.getNPCs().get(args[1]);
						NPCTrait traits = data.getTraits();
						sender.sendMessage(main.format("&b&M&L                      &6 NPC Info &b&M&L                      "));
						sender.sendMessage(main.format("&6Name: &F"+data.getName()));
						sender.sendMessage(main.format("&6Display Name: &F"+traits.getDisplayName()));
						sender.sendMessage(main.format("&6Subtitle: &F"+traits.getSubtitle()));
						sendJSONMessage(sender, getLocationComponents(args[1], data.getLoc()));
						sender.sendMessage(main.format("&6Skin: &F"+traits.getSkin()));
						sender.sendMessage(main.format("&6Range: &F"+traits.getRange()));
						sender.sendMessage(main.format("&6Head Rotation: &F"+traits.hasHeadRotation()));
						sender.sendMessage(main.format("&b&M&L                                                       "));
						return true;
					} else {
						sender.sendMessage(ChatColor.RED+"An NPC with that name does not exist.");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED+"Usage: /npc info <name>");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("tpto")) {
				if (sender instanceof Player) {
					Player p = (Player)sender;
					if (args.length == 2) {
						if (main.npc.getNPCs().containsKey(args[1])) {
							NPCData data = main.npc.getNPCs().get(args[1]);
							p.teleport(data.getLoc());
							p.sendMessage(main.format("&6Teleported to NPC &F"+data.getName()));
							return true;
						}
					}
				}
			}
			else if (args[0].equalsIgnoreCase("create")) {
				if (sender instanceof Player) {
					if (args.length == 2) {
						String name = args[1];
						if (name.length() > 16) {
							sender.sendMessage(ChatColor.RED+"The NPC's name identifier cannot be longer than 16 characters.");
							return true;
						}
						if (!main.npc.getNPCs().containsKey(name)) {
							main.npc.createNPC(name, ((Player)sender).getLocation());
							sender.sendMessage(main.format("&6Created an NPC named &F")+name);
							return true;
						} else {
							sender.sendMessage(main.format("&CAn NPC with this name already exists!"));
							return true;
						}
					}
					else if (args.length > 2) {
						sender.sendMessage(ChatColor.RED+"The NPC's name identifier can only be one word long!");
						return true;
					}
					else {
						sender.sendMessage(ChatColor.RED+"Usage: /npc create <name>");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED+"Sorry console, but you can't do that.");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("modify")) {
				if (args.length > 3) {
					String name = args[1];
					if (main.npc.getNPCs().containsKey(name)) {
						NPCData modifying = main.npc.getNPCs().get(name);
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
							sender.sendMessage(main.format(t.getMessage()));
						}
						main.npc.saveNPC(modifying);
						main.npc.updateNPC(modifying);
						return true;
					} else {
						sender.sendMessage(main.format("&CAn NPC with that name does not exist!"));
						return true;
					}
				} else {
					sender.sendMessage(main.format("&CUsage: /npc modify <name> <key> <value>"));
				}
			}
			else if (args[0].equalsIgnoreCase("move")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args.length == 2) {
						String name = args[1];
						if (main.npc.getNPCs().containsKey(name)) {
							NPCData data = main.npc.getNPCs().get(name);
							Location loc = p.getLocation();
							main.npc.moveNPC(data, loc);
							p.sendMessage(main.format("&6Moved the NPC named &F")+name+main.format("&6 to your position."));
							return true;
						} else {
							p.sendMessage(main.format("&CAn NPC with that name does not exist!"));
							return true;
						}
					} else {
						p.sendMessage(ChatColor.RED+"Usage: /npc move <name>");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED+"Sorry console, but you can't do that.");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length == 2) {
					String name = args[1];
					if (main.npc.getNPCs().containsKey(name)) {
						main.npc.removeNPC(name, true);
						main.npc.getNPCs().remove(name);
						if (main.npc.getNPCs().isEmpty())
							main.npcFile.getConfig().set("npc", null);
						sender.sendMessage(main.format("&6Removed the NPC named &F")+name);
						return true;
					} else {
						sender.sendMessage(main.format("&CAn NPC with that name does not exist!"));
						return true;
					}
				}
				else {
					sender.sendMessage(ChatColor.RED+"Usage: /npc remove <name>");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("list")) {
				if (!main.npc.getNPCs().isEmpty()) {
					sender.sendMessage(main.format("&6List of all NPC's &7&o(Click to Remove)"));
					TextComponent spacer = new TextComponent(" - ");
					spacer.setColor(ChatColor.AQUA);
					for (String npc : main.npc.getNPCs().keySet()) {
						sendJSONMessage(sender, getListComponents(npc));
					}
				} else {
					sender.sendMessage(main.format("&CThere are no NPC's!"));
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("clear")) {
				if (!main.npc.getNPCs().isEmpty()) {
					for (String npc : main.npc.getNPCs().keySet()) {
						main.npc.removeNPC(npc, false);
					}
					main.npcFile.getConfig().set("npc", null);
					main.npcFile.saveConfig();
					main.npc.getNPCs().clear();
					sender.sendMessage(main.format("&6Removed all NPC's."));
				} else {
					sender.sendMessage(main.format("&CThere are no NPC's to remove!"));
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("reload")) {
				sender.sendMessage(ChatColor.GOLD+"The plugin was reloaded.");
				main.reloadPlugin();
				return true;
			}
		}
		return false;
	}

	
	@SuppressWarnings("deprecation")
	private TextComponent[] getListComponents(String npc) {
		TextComponent component0 = new TextComponent(" - ");
		component0.setColor(ChatColor.GOLD);
		TextComponent component1 = new TextComponent(npc);
		component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/npc remove "+npc));
		component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(main.format("&7&oClick to remove this NPC.")).create()));
		component1.setColor(ChatColor.AQUA);
		return new TextComponent[] {component0, component1};
	}
	
	@SuppressWarnings("deprecation")
	private TextComponent[] getLocationComponents(String npc, Location loc) {
		TextComponent component0 = new TextComponent("Location: ");
		component0.setColor(ChatColor.GOLD);
		TextComponent component1 = new TextComponent("[World: "+loc.getWorld().getName()+" , X: "+loc.getX()+" , Y: "+loc.getY()+" , Z: "+loc.getZ()+"]");
		component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc tpto "+npc));
		component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(main.format("&7&oClick to teleport to this NPC.")).create()));
		component1.setColor(ChatColor.YELLOW);
		return new TextComponent[]{component0, component1};
	}

	private void sendJSONMessage(CommandSender sender, TextComponent...components) {
		if (sender instanceof Player) {
			((Player)sender).spigot().sendMessage(components);
		} else {
			String message = "";
			for (TextComponent component : components) {
				message += component.getText();
			}
			sender.sendMessage(message);
		}
	}
}
