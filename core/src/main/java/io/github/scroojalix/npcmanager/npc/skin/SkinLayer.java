package io.github.scroojalix.npcmanager.npc.skin;

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
}
