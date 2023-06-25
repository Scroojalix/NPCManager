package io.github.scroojalix.npcmanager.npc.meta;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;

public enum Pose {
    STANDING,
    CROUCHING(EntityPose.CROUCHING),
    SWIMMING(EntityPose.SWIMMING),
    SLEEPING(EntityPose.SLEEPING);

    private final EntityPose protocolValue;

    private Pose() {
        this(EntityPose.STANDING);
    }

    private Pose(EntityPose protocolValue) {
        this.protocolValue = protocolValue;
    }

    public Object getNMSValue() {
        return protocolValue.toNms();
    }
}
