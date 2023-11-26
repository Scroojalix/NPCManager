package io.github.scroojalix.npcmanager.npc.interactions;

/**
 * Enum containing the types of interactions involved in an {@link InteractAtNPCEvent}
 */
public enum NPCAction {
    LEFT_CLICK,
    RIGHT_CLICK,
    SECONDARY_LEFT_CLICK,
    SECONDARY_RIGHT_CLICK;

    public static NPCAction get(boolean isLeft, Boolean secondary) {
        if (secondary == null || !secondary.booleanValue()) {
            return isLeft ? LEFT_CLICK : RIGHT_CLICK;
        }
        return isLeft ? SECONDARY_LEFT_CLICK : SECONDARY_RIGHT_CLICK;
    }

    public boolean isLeftClick() {
        return this == LEFT_CLICK || this == SECONDARY_LEFT_CLICK;
    }

    public boolean isRightClick() {
        return this == RIGHT_CLICK || this == SECONDARY_RIGHT_CLICK;
    }
}
