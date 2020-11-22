package io.github.scroojalix.npcmanager.utils;

import org.bukkit.ChatColor;

public class Messages {

    //Error Messages
    public static String UNKNOWN_NPC = ChatColor.RED+"An NPC with that name does not exist!";
    public static String NPC_EXISTS = ChatColor.RED+"An NPC with that name already exists!";
    public static String LONG_NAME = ChatColor.RED+"An NPC's name cannot be longer than 16 characters.";
    public static String NO_NPCS = ChatColor.RED+"There are no NPC's!";

    //Command Messages
    public static String REMOVE_NPC = ChatColor.GOLD+"Removed an NPC named "+ChatColor.WHITE;

}
