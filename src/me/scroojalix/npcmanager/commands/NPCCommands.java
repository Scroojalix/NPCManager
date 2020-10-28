package me.scroojalix.npcmanager.commands;

import java.text.DecimalFormat;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.api.InteractionsManager;
import me.scroojalix.npcmanager.commands.tab.NPCCommandTab;
import me.scroojalix.npcmanager.utils.NPCData;
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
			if (args[0].equalsIgnoreCase("info")) {
				if (args.length == 2) {
					if (main.npc.getNPCs().containsKey(args[1])) {
						NPCData data = main.npc.getNPCs().get(args[1]);
						sender.sendMessage(main.format("&b&M&L                      &6 NPC Info &b&M&L                      "));
						sender.sendMessage(main.format("&6Name: &F"+data.getName()));
						sender.sendMessage(main.format("&6Display Name: &F"+data.getDisplayName()));
						sender.sendMessage(main.format("&6Subtitle: &F"+data.getSubtitle()));
						sender.spigot().sendMessage(getLocationComponents(args[1], data.getLoc()));
						sender.sendMessage(main.format("&6Skin: &F"+data.getSkin()));
						sender.sendMessage(main.format("&6Range: &F"+data.getRange()));
						sender.sendMessage(main.format("&6Head Rotation: &F"+data.hasHeadRotation()));
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
			if (args[0].equalsIgnoreCase("tpto")) {
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
			if (args[0].equalsIgnoreCase("create")) {
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
			if (args[0].equalsIgnoreCase("modify")) {
				if (args.length >= 3) {
					String name = args[1];
					if (main.npc.getNPCs().containsKey(name)) {
						NPCData modifying = main.npc.getNPCs().get(name);
						switch(args[2]) {
						case "displayName":
							if (args.length > 3) {
								StringBuilder displayName = new StringBuilder(args[3]);
								for (int arg = 4; arg < args.length; arg++) {
									displayName.append(" ").append(args[arg]);
								}
								boolean isEmpty = ChatColor.stripColor(main.format(displayName.toString())).isEmpty();
								modifying.setDisplayName(isEmpty?null:displayName.toString());
								main.npc.updateNPC(modifying);
								if (isEmpty) {
									sender.sendMessage(main.format("&CThat string is empty. The display name will not be visible."));
								} else {
									sender.sendMessage(main.format("&6Set the display name of &F")+name+main.format("&6 to &F"+displayName));
								}
							} else {
								modifying.setDisplayName(null);
								main.npc.updateNPC(modifying);
								sender.sendMessage(main.format("&CThat string is empty. The display name will not be visible."));
							}
							return true;
						case "subtitle":
							if (args.length > 3) {
								StringBuilder subtitle = new StringBuilder(args[3]);
								for (int arg = 4; arg < args.length; arg++) {
									subtitle.append(" ").append(args[arg]);
								}
								boolean isEmpty = ChatColor.stripColor(main.format(subtitle.toString())).isEmpty();
								modifying.setSubtitle(isEmpty?null:subtitle.toString());
								main.npc.updateNPC(modifying);
								if (isEmpty) {
									sender.sendMessage(main.format("&CThat string is empty. The subtitle will not be visible."));
								} else {
									sender.sendMessage(main.format("&6Set the subtitle of &F")+name+main.format("&6 to &F"+subtitle));
								}
							} else {
								modifying.setSubtitle(null);
								main.npc.updateNPC(modifying);
								sender.sendMessage(main.format("&CThat string is empty. The subtitle will not be visible."));
							}
							return true;
						case "hasHeadRotation":
							if (args.length == 3) {
								sender.sendMessage(ChatColor.RED+"Usage: /npc modify <name> <key> <value>");
							}
							else {
								modifying.setHasHeadRotation(args[3].equalsIgnoreCase("true"));
								main.npc.updateNPC(modifying);
								sender.sendMessage(main.format("&6Set the head rotation of &F")+name+main.format("&6 to &F"+String.valueOf(modifying.hasHeadRotation())));
							}
							return true;
						case "range":
							if (args.length == 3) {
								sender.sendMessage(ChatColor.RED+"Usage: /npc modify <name> <key> <value>");
							}
							else {
								try {
									Integer range = Integer.parseInt(args[3]);
									if (range <= 0) {
										sender.sendMessage(ChatColor.RED+"Range cannot be set to 0 or below!");
										return true;
									}
									modifying.setRange(range);
									main.npc.updateNPC(modifying);
									sender.sendMessage(main.format("&6Set the range of &F")+name+main.format("&6 to &F"+range));
								} catch(Exception e) {
									sender.sendMessage(ChatColor.RED+"'"+args[3]+"' is not a number");
								}
							}
							return true;
						case "skin":
							if (args.length == 3) {
								sender.sendMessage(ChatColor.RED+"Usage: /npc modify <name> <key> <value>");
							}
							else {
								if (args[3].equalsIgnoreCase("Default")) {
									main.npc.setSkin(modifying, null);
									sender.sendMessage(main.format("&6Set the skin of &F")+name+main.format("&6 to &FDefault"));
								} else if (main.skinManager.values().contains(args[3])) {
									main.npc.setSkin(modifying, args[3]);
									sender.sendMessage(main.format("&6Set the skin of &F")+name+main.format("&6 to &F"+args[3]));
								} else {
									sender.sendMessage(ChatColor.RED+"'"+args[3]+"' is not a valid skin.");
								}
							}
							return true;
						case "interactEvent":
							if (args.length == 3) {
								sender.sendMessage(ChatColor.RED+"Usage: /npc modify <name> <key> <value>");
							}
							else {
								String interaction = args[3];
								if (!interaction.equalsIgnoreCase("None")) {
									if (InteractionsManager.getInteractEvents().containsKey(interaction)) {
										modifying.setInteractEvent(InteractionsManager.getInteractEvents().get(interaction));
										sender.sendMessage(main.format("&6Set the interact event of &F")+name+main.format("&6 to &F"+interaction));
										main.npc.updateNPC(modifying);
									} else {
										sender.sendMessage(main.format("&C'"+interaction+"' is not a valid interact event."));
									}
								} else {
									modifying.setInteractEvent(null);
									sender.sendMessage(main.format("&6Set the interact event of &F")+name+main.format("&6 to &FNone"));
									main.npc.updateNPC(modifying);
								}
							}
							return true;
						default:
							sender.sendMessage(ChatColor.RED+"That is not a valid key to modify.");
							return true;
						}
					} else {
						sender.sendMessage(main.format("&CAn NPC with that name does not exist!"));
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED+"Usage: /npc modify <name> <key> <value>");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("move")) {
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
			if (args[0].equalsIgnoreCase("remove")) {
				if (args.length == 2) {
					String name = args[1];
					if (main.npc.getNPCs().containsKey(name)) {
						main.npc.removeNPC(name);
						main.npc.getNPCs().remove(name);
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
			if (args[0].equalsIgnoreCase("list")) {
				if (!main.npc.getNPCs().isEmpty()) {
					sender.sendMessage(main.format("&6List of all NPC's &7&o(Click to Remove)"));
					TextComponent spacer = new TextComponent(" - ");
					spacer.setColor(net.md_5.bungee.api.ChatColor.AQUA);
					for (String npc : main.npc.getNPCs().keySet()) {
						sender.spigot().sendMessage(spacer, getListComponent(npc));
					}
				} else {
					sender.sendMessage(main.format("&CThere are no NPC's!"));
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("clear")) {
				if (!main.npc.getNPCs().isEmpty()) {
					for (String npc : main.npc.getNPCs().keySet()) {
						main.npc.removeNPC(npc);
					}
					main.npc.getNPCs().clear();
					sender.sendMessage(main.format("&6Removed all NPC's."));
				} else {
					sender.sendMessage(main.format("&CThere are no NPC's to remove!"));
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				main.reloadPlugin();
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.GOLD+"The plugin was reloaded.");
				} else {
					main.log(Level.INFO, "The plugin was reloaded.");
				}
				return true;
			}
		}
		return false;
	}

	
	@SuppressWarnings("deprecation")
	private TextComponent getListComponent(String npc) {
		TextComponent component = new TextComponent(npc);
		component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/npc remove "+npc));
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(main.format("&7&oClick to remove this NPC.")).create()));
		component.setColor(net.md_5.bungee.api.ChatColor.GREEN);
		return component;
	}
	
	@SuppressWarnings("deprecation")
	private TextComponent[] getLocationComponents(String npc, Location loc) {
		TextComponent component0 = new TextComponent("Location: ");
		component0.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		DecimalFormat df = new DecimalFormat("##.##");
		TextComponent component1 = new TextComponent("[World: "+loc.getWorld().getName()+" , X: "+df.format(loc.getX())+" , Y: "+df.format(loc.getY())+" , Z: "+df.format(loc.getZ())+"]");
		component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc tpto "+npc));
		component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(main.format("&7&oClick to teleport to this NPC.")).create()));
		component1.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
		return new TextComponent[]{component0, component1};
	}
}
