package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nonnull;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

public class NPCMetaInfo implements Serialisable {

    @Expose
    private @Nonnull Pose pose;
    @Expose
    private @Nonnull HandState handState;
    
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
    private @Nonnull GlowColor glowColor;
    @Expose
    private boolean collision;

    public NPCMetaInfo() {
        this.pose = Pose.STANDING;
        this.handState = new HandState(false, Hand.MAIN_HAND, false);
        this.glowColor = GlowColor.WHITE;
        collision = false;
    }

    public Pose getPose() {
        return this.pose;
    }

    public void setPose(@Nonnull Pose pose) {
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

    public void setGlowColor(@Nonnull GlowColor glowColor) {
        this.glowColor = glowColor;
    }

    // GETTERS

    public boolean isShivering() {
        return this.shivering;
    }

    public boolean isGlowingEnabled() {
        return this.glowing;
    }

    public GlowColor getGlowColor() {
        return this.glowColor;
    }

    public boolean isCollisionEnabled() {
        return this.collision;
    }
}
