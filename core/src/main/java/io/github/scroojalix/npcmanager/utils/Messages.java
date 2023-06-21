package io.github.scroojalix.npcmanager.utils;

public class Messages {

    //Error Messages
    public static final String UNKNOWN_NPC = "An NPC with that name does not exist!";
    public static final String NPC_EXISTS = "An NPC with that name already exists!";
    public static final String LONG_NAME = "An NPC's name cannot be longer than 16 characters.";
    public static final String NOT_ALPHANUMERIC = "An NPC name must only contain alphanumeric characters and underscores!";
    public static final String NO_NPCS = "There are no NPC's!";
    public static final String DATABASE_NOT_CONNECTED = "Could not establish a connection to the database. Check that the database is online and the login info is correct, then run /npc reload.";
    public static final String RESOLVE_ERRORS = "Please resolve this error and then run \"/npc reload\"";

    //Other
    public static final String RESTORE_NPCS = "Restoring NPC's...";

    public static String getNPCRestoreError(String npcName, String reason) {
        return String.format("Error restoring \"%s\": %s", npcName, reason);
    } 

}
