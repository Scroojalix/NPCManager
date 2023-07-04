package io.github.scroojalix.npcmanager.npc.meta.enums;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;

public enum ActiveHand {
    MAIN_HAND("MainHand", Hand.MAIN_HAND),
    OFF_HAND("OffHand", Hand.OFF_HAND),
    NONE("None");

    private final String tag;
    private final Hand nmsHand;
    
    ActiveHand(String tag) {
        this(tag, Hand.MAIN_HAND);
    }

    ActiveHand(String tag, Hand nmsHand) {
        this.tag = tag;
        this.nmsHand = nmsHand;
    }

    public Hand getNMSHand() {
        return nmsHand;
    }

    @Override
    public String toString() {
        return tag;
    }
}