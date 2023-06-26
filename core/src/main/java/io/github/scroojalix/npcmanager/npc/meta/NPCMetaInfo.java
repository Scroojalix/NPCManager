package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nonnull;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

/**
 * Store all metadata for an NPC. Handles per version metadata format.
 * This is an absolute mess of code... but I don't know how else to do it.
 * @author Scroojalix
 * @see https://wiki.vg/Entity_metadata
 */
public class NPCMetaInfo implements Serialisable {

    // Entity Metadata at Index 0 of Data Watcher

    /**
     * This flag is valid for all versions<p>
     * Bitmask = 0x01
     */
    @Expose
    private boolean onFire;
    
    /**
     * This flag is set in {@code pose} for 1.14+ servers<p>
     * Bitmask = 0x02
     */
    @Expose
    private boolean crouching;

    /**
     * This flag is unused in 1.12+ servers.<p>
     * Bitmask = 0x04
     */
    @Expose
    private boolean sitting;

    /**
     * This flag is valid for all versions
     * Bitmask = 0x08
     */
    @Expose
    private boolean sprinting;

    /**
     * This flag is active for versions 1.8 to 1.10.<p>
     * For 1.13 servers, it is used to represent swimming.<p>
     * Unused otherwise. Use {@code handState} instead.<p>
     * Bitmask = 0x10
     */
    @Expose
    private boolean blocking;

    /**
     * This flag is valid for all versions
     * Bitmask = 0x20
     */
    @Expose
    private boolean invisible;

    /**
     * This flag does nothing in 1.8 servers
     * Bitmask = 0x40
     */
    @Expose
    private boolean glowing;

    /**
     * This flag does nothing in 1.8 servers
     * Bitmask = 0x80
     */
    @Expose
    private boolean elytra;

    /**
     * This field will only have an affect on 1.14+ servers<p>
     * Index = 6
     */
    @Expose
    private @Nonnull Pose pose;
    
    /*
     * This field is only active on 1.17+ servers<p>
     * Index = 7
     */
    @Expose
    private boolean shivering;

    /**
     * Contains information on the hand state.<p>
     * Only used in 1.9+ servers.
     * @see HandState
     */
    @Expose
    private @Nonnull HandState handState;

    /**
     * Sent as part of the scoreboard team packet
     */
    @Expose
    private @Nonnull GlowColor glowColor;
    /**
     * Sent as part of the scoreboard team packet
     */
    @Expose
    private boolean collision;

    public NPCMetaInfo() {
        this.pose = Pose.STANDING;
        this.handState = new HandState(false, Hand.MAIN_HAND, false);
        this.glowColor = GlowColor.WHITE;
    }

    public Pose getPose() {
        return this.pose;
    }

    public void setPose(@Nonnull Pose pose) {
        this.pose = pose;
    }

    /**
     * Get the byte to be stored at index 0 of the NPC's
     * datawatcher
     * @see https://wiki.vg/Entity_metadata#Entity
     * @return Entity Base Metadata byte
     */
    public byte getEntityMetaByte() {
        int fire   = onFire    ? 0x01 : 0;
        int sprint = sprinting ? 0x08 : 0;
        int invis  = invisible ? 0x20 : 0;
        int glow   = glowing   ? 0x40 : 0;
        int fly    = elytra    ? 0x80 : 0;
        return (byte) (fire | sprint | invis | glow | fly);
    }

    public HandState getHandState() {
        return this.handState;
    }

    // FLAG SETTERS

    public void setOnFire(boolean value) {
        this.onFire = value;
    }

    public void setSprinting(boolean value) {
        this.sprinting = value;
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
