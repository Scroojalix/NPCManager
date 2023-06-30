package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;

import io.github.scroojalix.npcmanager.NPCMain;

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

    public String getColorName() {
        return colorName;
    }

    public int getHexValue() {
        return hex;
    }

    public static @Nullable MetaColor getMetaColorFromName(String name) {
        for (MetaColor color : MetaColor.values()) {
            if (color.colorName.equalsIgnoreCase(name))
                return color;
        }
        return null;
    }

    public static int getPotionColorIndex() {
        switch(NPCMain.serverVersion) {
            case v1_8_R2:
            case v1_8_R3:
            case v1_9_R1:
            case v1_9_R2:
                return 7;
            case v1_10_R1:
            case v1_11_R1:
            case v1_12_R1:
            case v1_13_R1:
            case v1_13_R2:
                return 8;
            case v1_14_R1:
            case v1_15_R1:
            case v1_16_R1:
            case v1_16_R2:
            case v1_16_R3:
                return 9;
            case v1_17_R1:
            case v1_18_R1:
            case v1_18_R2:
            case v1_19_R1:
            case v1_19_R2:
            case v1_19_R3:
            case v1_20_R1:
                return 10;
            default:
                throw new IllegalArgumentException("That version does not have potion color meta");
        }
    }
}