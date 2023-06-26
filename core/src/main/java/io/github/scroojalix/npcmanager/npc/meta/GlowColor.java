package io.github.scroojalix.npcmanager.npc.meta;

import org.bukkit.ChatColor;

/**
 * All available glow colors and there corresponding Bukkit value.
 * This is done to limit the colours that can be chosen from so the
 * user cannot use an invalid value by mistake.
 * 
 * @author Scroojalix
 */
public enum GlowColor {
    BLACK(ChatColor.BLACK),
    DARK_BLUE(ChatColor.DARK_BLUE),
    DARK_GREEN(ChatColor.DARK_GREEN),
    DARK_AQUA(ChatColor.DARK_AQUA),
    DARK_RED(ChatColor.DARK_RED),
    DARK_PURPLE(ChatColor.DARK_PURPLE),
    GOLD(ChatColor.GOLD),
    GRAY(ChatColor.GRAY),
    DARK_GRAY(ChatColor.DARK_GRAY),
    BLUE(ChatColor.BLUE),
    GREEN(ChatColor.GREEN),
    AQUA(ChatColor.AQUA),
    RED(ChatColor.RED),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE),
    YELLOW(ChatColor.YELLOW),
    WHITE(ChatColor.WHITE);

    private final ChatColor bukkitColor;
    
    private GlowColor(ChatColor bukkitColor) {
        this.bukkitColor = bukkitColor;
    }

    public ChatColor getBukkitColor() {
        return this.bukkitColor;
    }
}
