package io.github.scroojalix.npcmanager.npc;

import org.bukkit.ChatColor;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

public class NPCMetaInfo implements Serialisable {

    @Expose
    private Pose pose;
    @Expose
    private HandState handState;
    
    // Entity Metadata
    // Source: https://wiki.vg/Entity_metadata#Entity
    @Expose
    private boolean onFire;     // 0x01
    @Expose
    private boolean invisible;  // 0x20
    @Expose
    private boolean glowing;    // 0x40
    @Expose
    private boolean elytra;     // 0x80
    
    @Expose
    private boolean shivering;

    @Expose
    private ChatColor glowColor;
    @Expose
    private boolean collision;

    NPCMetaInfo() {
        this.pose = Pose.STANDING;
        this.handState = new HandState(false, Hand.MAIN_HAND, false);
        this.glowColor = ChatColor.WHITE;
    }

    public Pose getPose() {
        return this.pose;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    public byte getEntityMetaByte() {
        int fire  = onFire    ? 0x01 : 0;
        int invis = invisible ? 0x20 : 0;
        int glow  = glowing   ? 0x40 : 0;
        int fly   = elytra    ? 0x80 : 0;
        return (byte) (fire | invis | glow | fly);
    }

    public HandState getHandState() {
        return this.handState;
    }

    // FLAG SETTERS

    public void setOnFire(boolean value) {
        this.onFire = value;
    }

    public void setInvisible(boolean value) {
        this.invisible = value;
    }

    public void setGlowing(boolean value) {
        this.glowing = value;
    }

    public void setElytraFlying(boolean value) {
        this.elytra = value;
    }

    public void setShivering(boolean value) {
        this.shivering = value;
    }

    public void setCollision(boolean value) {
        this.collision = value;
    }

    public void setGlowColor(ChatColor glowColor) {
        this.glowColor = glowColor;
    }

    // GETTERS

    public boolean isShivering() {
        return shivering;
    }

    public ChatColor getGlowColor() {
        return this.glowColor;
    }

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

    public static class HandState implements Serialisable {

        @Expose
        private boolean active;
        @Expose
        private Hand hand;
        @Expose
        private boolean isRiptideSpinAttack;

        HandState() {}

        private HandState(boolean active, Hand hand, boolean riptide) {
            this.active = active;
            this.hand = hand;
            this.isRiptideSpinAttack = riptide;
        }

        public void set(boolean active, Hand hand) {
            this.active = active;
            this.hand = hand;
        }

        public void setIsRiptideSpin(boolean isRiptideSpinAttack) {
            this.isRiptideSpinAttack = isRiptideSpinAttack;
        }

        public byte getByteFlag() {
            int flag1 = active ? 0x1 : 0;
            int flag2 = hand.ordinal() == 0 ? 0 : 0x2;
            int flag3 = isRiptideSpinAttack ? 0x4 : 0;
            return (byte)(flag1 | flag2 | flag3);
        }
    }
}
