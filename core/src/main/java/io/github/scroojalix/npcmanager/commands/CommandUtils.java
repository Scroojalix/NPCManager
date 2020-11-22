package io.github.scroojalix.npcmanager.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
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
			sender.sendMessage(Messages.UNKNOWN_NPC);
			return false;
		}
	}

    @SuppressWarnings("deprecation")
	public static TextComponent[] getListComponents(String npc) {
		TextComponent component0 = new TextComponent(" - ");
		component0.setColor(ChatColor.GOLD);
		TextComponent component1 = new TextComponent(npc);
		component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/npc remove "+npc));
		component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PluginUtils.format("&7&oClick to remove this NPC.")).create()));
		component1.setColor(ChatColor.AQUA);
		return new TextComponent[] {component0, component1};
	}
	
	@SuppressWarnings("deprecation")
	public static TextComponent[] getLocationComponents(String npc, Location loc) {
		TextComponent component0 = new TextComponent("Location: ");
		component0.setColor(ChatColor.GOLD);
		TextComponent component1 = new TextComponent("[World: "+loc.getWorld().getName()+" , X: "+loc.getX()+" , Y: "+loc.getY()+" , Z: "+loc.getZ()+"]");
		component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc tpto "+npc));
		component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(PluginUtils.format("&7&oClick to teleport to this NPC.")).create()));
		component1.setColor(ChatColor.YELLOW);
		return new TextComponent[]{component0, component1};
	}

	public static void sendJSONMessage(CommandSender sender, TextComponent...components) {
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
