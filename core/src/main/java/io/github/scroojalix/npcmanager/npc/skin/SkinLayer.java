package io.github.scroojalix.npcmanager.npc.skin;

import io.github.scroojalix.npcmanager.NPCMain;

public enum SkinLayer {
    CAPE("--cape"),
    JACKET("--jacket"),
    LEFT_SLEEVE("--leftSleeve"),
    RIGHT_SLEEVE("--rightSleeve"),
    LEFT_LEG("--leftLeg"),
    RIGHT_LEG("--rightLeg"),
    HAT("--hat");

    public final String label;

    private SkinLayer(String label) {
        this.label = label;
    }

    // TODO maybe a better way of implementing this?
    // Maybe move all meta index getters to own class
    public static int getSkinLayersByteIndex() {
        switch(NPCMain.serverVersion) {
            case v1_8_R2:
            case v1_8_R3:
                return 10;
            case v1_9_R1:
            case v1_9_R2:
                return 12;
            case v1_10_R1:
            case v1_11_R1:
            case v1_12_R1:
            case v1_13_R1:
            case v1_13_R2:
                return 13;
            case v1_14_R1:
                return 15;
            case v1_15_R1:
            case v1_16_R1:
            case v1_16_R2:
            case v1_16_R3:
                return 16;
            case v1_17_R1:
            case v1_18_R1:
            case v1_18_R2:
            case v1_19_R1:
            case v1_19_R2:
            case v1_19_R3:
            case v1_20_R1:
                return 17;
            default:
                // This should never get called. It is only here to alert me if I
                // forget to update this.
                throw new IllegalArgumentException("Unknown Enum Value for ServerVersion: " + NPCMain.serverVersion.toString());
        }
    }
}
