package io.github.scroojalix.npcmanager.npc.meta;

import org.bukkit.ChatColor;

/**
 * All available colors, and there corresponding Bukkit and hex value.
 * This is done to limit the colours that can be chosen from so the
 * user cannot use an invalid value by mistake.
 * 
 * @see https://wiki.vg/Chat#Colors for hex code source
 * 
 * @author Scroojalix
 */
public enum MetaColor {
    BLACK(ChatColor.BLACK, "Black", 0x000001), // Have to write a 1 instead of 0 for black, as 0 corresponds to no colour
    DARK_BLUE(ChatColor.DARK_BLUE, "DarkBlue", 0x0000aa),
    DARK_GREEN(ChatColor.DARK_GREEN, "DarkGreen", 0x00aa00),
    DARK_AQUA(ChatColor.DARK_AQUA, "DarkAqua", 0x00aaaa),
    DARK_RED(ChatColor.DARK_RED, "DarkRed", 0xaa0000),
    DARK_PURPLE(ChatColor.DARK_PURPLE, "DarkPurple", 0xaa00aa),
    GOLD(ChatColor.GOLD, "Gold", 0xffaa00),
    GRAY(ChatColor.GRAY, "Gray", 0xaaaaaa),
    DARK_GRAY(ChatColor.DARK_GRAY, "DarkGray", 0x555555),
    BLUE(ChatColor.BLUE, "Blue", 0x5555ff),
    GREEN(ChatColor.GREEN, "Green", 0x55ff55),
    AQUA(ChatColor.AQUA, "Aqua", 0x55ffff),
    RED(ChatColor.RED, "Red", 0xff5555),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, "LightPurple", 0xff55ff),
    YELLOW(ChatColor.YELLOW, "Yellow", 0xffff55),
    WHITE(ChatColor.WHITE, "White", 0xffffff),
    NONE(ChatColor.RESET, "None", 0);

    private final ChatColor bukkitColor;
    private final String colorName;
    private final int hex;

    private MetaColor(ChatColor bukkitColor, String commandFormat, int hex) {
        this.bukkitColor = bukkitColor;
        this.colorName = commandFormat;
        this.hex = hex;
    }

    public ChatColor getBukkitColor() {
        return this.bukkitColor;
    }

    public int getHexValue() {
        return hex;
    }

    @Override
    public String toString() {
        return colorName;
    }
}
