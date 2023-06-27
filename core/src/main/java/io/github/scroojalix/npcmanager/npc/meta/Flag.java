package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nullable;

import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

public enum Flag {
    ON_FIRE("onFire"),
    SPRINTING("sprinting"),
    INVISIBLE("invisible"),
    GLOWING("glowingEnabled", ServerVersion.v1_9_R1),
    ELYTRA_ENABLED("elytraEnabled", ServerVersion.v1_9_R1),
    SHIVERING("shivering", ServerVersion.v1_17_R1),
    COLLISION("collisionEnabled", ServerVersion.v1_9_R1),
    GLOW_COLOR("glowColor", ServerVersion.v1_9_R1, GlowColor.class);

    private final String tag;
    private final ServerVersion minVer;
    private final Class<?> valueClass;

    private Flag(String tag) {
        this(tag, ServerVersion.v1_8_R2);
    }

    private Flag(String tag, ServerVersion minVer) {
        this(tag, minVer, Boolean.class);
    }
    
    private Flag(String tag, ServerVersion minVer, Class<?> valueClass) {
        this.tag = tag;
        this.minVer = minVer;
        this.valueClass = valueClass;
    } 

    public String getCommandTag() {
        return tag;
    }

    public boolean isEnabled() {
        return minVer.atOrAbove();
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public static @Nullable Flag getFlagFromTag(String tag) {
        for (Flag flag : Flag.values()) {
            if (flag.tag.equalsIgnoreCase(tag))
                return flag;
        }
        return null;
    }
}
