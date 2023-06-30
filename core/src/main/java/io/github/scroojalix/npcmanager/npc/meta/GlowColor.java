package io.github.scroojalix.npcmanager.npc.meta;

import org.bukkit.ChatColor;

import com.mongodb.lang.Nullable;

/**
 * All available glow colors and there corresponding Bukkit value.
 * This is done to limit the colours that can be chosen from so the
 * user cannot use an invalid value by mistake.
 * 
 * @author Scroojalix
 */
public enum GlowColor {
    BLACK(ChatColor.BLACK, "Black"),
    DARK_BLUE(ChatColor.DARK_BLUE, "DarkBlue"),
    DARK_GREEN(ChatColor.DARK_GREEN, "DarkGreen"),
    DARK_AQUA(ChatColor.DARK_AQUA, "DarkAqua"),
    DARK_RED(ChatColor.DARK_RED, "DarkRed"),
    DARK_PURPLE(ChatColor.DARK_PURPLE, "DarkPurple"),
    GOLD(ChatColor.GOLD, "Gold"),
    GRAY(ChatColor.GRAY, "Gray"),
    DARK_GRAY(ChatColor.DARK_GRAY, "DarkGray"),
    BLUE(ChatColor.BLUE, "Blue"),
    GREEN(ChatColor.GREEN, "Green"),
    AQUA(ChatColor.AQUA, "Aqua"),
    RED(ChatColor.RED, "Red"),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, "LightPurple"),
    YELLOW(ChatColor.YELLOW, "Yellow"),
    WHITE(ChatColor.WHITE, "White"),
    NONE(null, "None");

    private final ChatColor bukkitColor;
    private final String colorName;
    
    private GlowColor(ChatColor bukkitColor, String commandFormat) {
        this.bukkitColor = bukkitColor;
        this.colorName = commandFormat;
    }

    public ChatColor getBukkitColor() {
        return this.bukkitColor;
    }

    public String getColorName() {
        return colorName;
    }

    public static @Nullable GlowColor getGlowColorFromName(String name) {
        for (GlowColor color : GlowColor.values()) {
            if (color.colorName.equalsIgnoreCase(name))
                return color;
        }
        return null;
    }
}
