package io.github.scroojalix.npcmanager.common.chatutils;

public class Messages {

    //Error Messages
    public static String UNKNOWN_NPC = "An NPC with that name does not exist!";
    public static String NPC_EXISTS = "An NPC with that name already exists!";
    public static String LONG_NAME = "An NPC's name cannot be longer than 16 characters.";
    public static String NOT_ALPHANUMERIC = "An NPC name must only contain alphanumeric characters and underscores!";
    public static String NO_NPCS = "There are no NPC's!";
    public static String DATABASE_NOT_CONNECTED = "Could not establish a connection to the database. Check that the database is online and the login info is correct, then run /npc reload.";

    //Other
    public static String RESTORE_NPCS = "Restoring NPC's...";

}
