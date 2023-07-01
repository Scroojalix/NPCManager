package io.github.scroojalix.npcmanager.npc.meta;

import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

public enum Flag {
    ON_FIRE("onFire"),
    SPRINTING("sprinting"),
    INVISIBLE("invisible"),
    AMBIENT_POTION_EFFECT("ambientPotionEffect"),
    ELYTRA_ENABLED("elytraEnabled", ServerVersion.v1_9_R1),
    SHIVERING("shivering", ServerVersion.v1_17_R1),
    RIPTIDE_SPIN("isRiptideSpinAttack", ServerVersion.v1_13_R1),
    COLLISION("collisionEnabled", ServerVersion.v1_9_R1);

    private final String tag;
    private final ServerVersion minVer;

    private Flag(String tag) {
        this(tag, ServerVersion.v1_8_R2);
    }

    private Flag(String tag, ServerVersion minVer) {
        this.tag = tag;
        this.minVer = minVer;
    }

    public boolean isEnabled() {
        return minVer.atOrAbove();
    }

    @Override
    public String toString() {
        return tag;
    }
}
