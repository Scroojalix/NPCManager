package io.github.scroojalix.npcmanager.npc.meta.enums;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;

public enum Pose {
    STANDING("Standing"),
    CROUCHING("Crouching", EntityPose.CROUCHING),
    SWIMMING("Swimming", EntityPose.SWIMMING),
    SLEEPING("Sleeping", EntityPose.SLEEPING),
    SITTING("Sitting", EntityPose.SITTING); //TODO sitting not working

    private final String name;
    private final EntityPose protocolValue;

    private Pose(String name) {
        this(name, EntityPose.STANDING);
    }

    private Pose(String name, EntityPose protocolValue) {
        this.name = name;
        this.protocolValue = protocolValue;
    }

    public Object getNMSValue() {
        return protocolValue.toNms();
    }

    @Override
    public String toString() {
        return name;
    }
}
