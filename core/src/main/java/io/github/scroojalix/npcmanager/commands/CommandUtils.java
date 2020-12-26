package io.github.scroojalix.npcmanager.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.npc.equipment.EmptySlots;
import io.github.scroojalix.npcmanager.utils.npc.equipment.EquipmentInventory;
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.chat.TextComponentWrapper;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandUtils {

	public static boolean npcExists(String name, CommandSender sender) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			return true;
		} else {
			sender.sendMessage(ChatColor.RED+Messages.UNKNOWN_NPC);
			return false;
		}
	}

	public static TextComponentWrapper[] getTitleMessage(String title) {
		TextComponentWrapper line = new TextComponentWrapper("                          ", true);
		line.setColor(ChatColor.AQUA); line.setBold(true); line.setStrikethrough(true); line.setIsLine();
		TextComponentWrapper label = new TextComponentWrapper(" "+title+" ", true);
		label.setColor(ChatColor.GOLD);
		return new TextComponentWrapper[] {line, label, line};
	}

    @SuppressWarnings("deprecation")
	public static TextComponentWrapper[] getListComponents(String npc) {
		TextComponentWrapper arrow = new TextComponentWrapper(" --> ", true);
		arrow.setColor(ChatColor.GRAY);
		TextComponentWrapper name = new TextComponentWrapper(npc, true);
		name.setColor(ChatColor.AQUA);
		TextComponentWrapper spacer1 = new TextComponentWrapper(PluginUtils.format(getSpacer(npc.length())), false);
		TextComponentWrapper info = new TextComponentWrapper("[INFO]", false);
		info.setColor(ChatColor.GREEN); info.setBold(true);
		info.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc info "+npc));
		info.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PluginUtils.format("&7&oClick to show this NPC's info.")).create()));
		TextComponentWrapper spacer2 = new TextComponentWrapper("                ", false);
		TextComponentWrapper remove = new TextComponentWrapper("[REMOVE]", false);
		remove.setColor(ChatColor.RED); remove.setBold(true);
		remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc remove "+npc));
		remove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PluginUtils.format("&7&oClick to remove this NPC.")).create()));
		return new TextComponentWrapper[] {arrow, name, spacer1, info, spacer2, remove};
	}
	
	@SuppressWarnings("deprecation")
	public static TextComponentWrapper[] getLocationComponents(String npc, Location loc) {
		TextComponentWrapper component0 = new TextComponentWrapper("Location: ", true);
		component0.setColor(ChatColor.GOLD);
		TextComponentWrapper component1 = new TextComponentWrapper("", true);
		component1.setText(PluginUtils.format("&eWorld: "+loc.getWorld().getName()+" &cX: "+loc.getX()+" &aY: "+loc.getY()+" &9Z: "+loc.getZ()));
		component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc tpto "+npc));
		component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PluginUtils.format("&7&oClick to teleport to this NPC.")).create()));
		return new TextComponentWrapper[]{component0, component1};
	}

	@SuppressWarnings("deprecation")
	public static TextComponentWrapper[] getEquipmentComponents(String npc) {
		TextComponentWrapper component0 = new TextComponentWrapper("Equipment: ", true);
		component0.setColor(ChatColor.GOLD);
		TextComponentWrapper component1 = new TextComponentWrapper("[Show Equipment Menu]", true);
		component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc modify "+npc+" equipment"));
		component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PluginUtils.format("&7&oClick to view this NPC's equipment menu.")).create()));
		component1.setColor(ChatColor.GREEN);
		return new TextComponentWrapper[]{component0, component1};
	}
	
	public static TextComponentWrapper[] getPageTurnerMessage(String command, int pages, int current) {
		boolean left = current > 1;
		boolean right = pages > current;
		TextComponentWrapper line = new TextComponentWrapper("                     ", true);
		line.setIsLine();
		line.setColor(ChatColor.AQUA); line.setBold(true); line.setStrikethrough(true);
		TextComponentWrapper leftArrow;
		if (left) {
			leftArrow = new TextComponentWrapper(" <-", true);
			leftArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (current-1)));
			leftArrow.setColor(ChatColor.GOLD);
		} else {
			leftArrow = new TextComponentWrapper("   ", true);
			leftArrow.setStrikethrough(true); leftArrow.setBold(true); leftArrow.setIsLine();
			leftArrow.setColor(ChatColor.AQUA);
		}
		TextComponentWrapper center = new TextComponentWrapper(PluginUtils.format(" &6Page &b"+current+" &6of &b"+pages+" "), true);
		TextComponentWrapper rightArrow;
		if (right) {
			rightArrow = new TextComponentWrapper("-> ", true);
			rightArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (current+1)));
			rightArrow.setColor(ChatColor.GOLD);
		} else {
			rightArrow = new TextComponentWrapper("   ", true);
			rightArrow.setStrikethrough(true); rightArrow.setBold(true); rightArrow.setIsLine();
			rightArrow.setColor(ChatColor.AQUA);
		}
		return new TextComponentWrapper[]{line, leftArrow, center, rightArrow, line};
	}

	public static void sendJSONMessage(CommandSender sender, TextComponentWrapper...components) {
		if (sender instanceof Player) {
			TextComponent[] message = new TextComponent[components.length];
			for (int i = 0; i < components.length; i++) {
				message[i] = components[i].getComponent();
			}
			((Player)sender).spigot().sendMessage(message);
		} else {
			String message = "";
			for (TextComponentWrapper component : components) {
				if (component.isVisibleToConsole()) {
					if (component.isLine()) {
						component.setText(component.getText().replace(" ", "-"));
					}
					message += component.getColor() + component.getText();
				}
			}
			sender.sendMessage(message);
		}
	}
	
	public static Inventory getEquipmentInv(NPCData data) {
		Inventory inv = Bukkit.createInventory(new EquipmentInventory(), 45, data.getName()+"'s Equipment");
		List<ItemStack> equipment = data.getTraits().getEquipment().getEquipmentArray();
		List<ItemStack> emptySlots = EmptySlots.getArray();
		int[] slots = {10, 12, 14, 16, 30, 32};

		for (int i = 0; i < equipment.size(); i++) {
			if (equipment.get(i) != null) {
				inv.setItem(slots[i], equipment.get(i));
			} else {
				inv.setItem(slots[i], emptySlots.get(i));
			}
		}

		//Filler Item
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) {
				inv.setItem(i, EmptySlots.fillerItem);
			}
		}
		return inv;
	}

	public static String getErrorSound() {
		switch(NPCMain.serverVersion.errorSoundId) {
			case 0:
				return "ENDERMAN_TELEPORT";
			case 1:
				return "ENTITY_ENDERMEN_TELEPORT";
			case 2:
				return "ENTITY_ENDERMAN_TELEPORT";
			default:
				return "";
		}
	}

	private static String getSpacer(int wordLength) {
		boolean even = wordLength % 2 == 0;
		int startLength = even?24:28;
		int multiplier = ((wordLength+(even?0:1))/2);
		int spaceLength = startLength - 3 * multiplier;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < spaceLength; i++) {
			result.append(" ");
		}
		if (even) {
			result.append("&l  ");
		}
		return result.toString();
	}
}
