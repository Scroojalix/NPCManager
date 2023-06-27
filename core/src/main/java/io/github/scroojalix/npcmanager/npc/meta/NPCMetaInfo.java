package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nonnull;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.storage.misc.Serialisable;
import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

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
     * The flag at index 1 is set in {@code pose} for 1.14+ servers<p>
     * Typically represents the NPC crouching<p>
     * Bitmask = 0x02
     */

    /**
     * The flag at index 2 is unused in 1.12+ servers.<p>
     * Typically represents the NPC sitting<p>
     * Bitmask = 0x04
     */

    /**
     * This flag is valid for all versions<p>
     * Bitmask = 0x08
     */
    @Expose
    private boolean sprinting;

    /**
     * Flag at index 4 is only used for versions 1.8 to 1.10.<p>
     * For 1.13 servers, it is used to represent swimming.<p>
     * Unused otherwise. Use {@code handState} instead.<p>
     * Bitmask = 0x10
     */

    /**
     * This flag is valid for all versions<p>
     * Bitmask = 0x20
     */
    @Expose
    private boolean invisible;

    /**
     * This flag does nothing in 1.8 servers<p>
     * Bitmask = 0x40
     */
    @Expose
    private boolean glowing;

    /**
     * This flag does nothing in 1.8 servers<p>
     * Bitmask = 0x80
     */
    @Expose
    private boolean elytraEnabled;

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
     * Refer to {@link HandState#getIndex()} for index<p>
     * Only used in 1.9+ servers.
     * @see HandState
     */
    @Expose
    private @Nonnull HandState handState;

    /**
     * Sent as part of the scoreboard team packet<p>
     * Has no effect on 1.8 servers
     */
    @Expose
    private @Nonnull GlowColor glowColor;
    /**
     * Sent as part of the scoreboard team packet<p>
     * Has no effect on 1.8 servers
     */
    @Expose
    private boolean collisionEnabled;

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
        int f0,f1,f2,f3,f4,f5,f6,f7;
        f0 = f1 = f2 = f3 = f4 = f5 = f6 = f7 = 0;

        // Flag 0
        if (onFire) f0 = 0x01;

        // Flag 1
        if (pose == Pose.CROUCHING) f1 = 0x02;

        // Flag 2
        if (pose == Pose.SITTING) f2 = 0x04;

        // Flag 3
        if (sprinting) f3 = 0x08;

        // Flag 4
        if ((handState.isActive())
            || (pose == Pose.SWIMMING && ServerVersion.v1_13_R1.atOrAbove()))
            f4 = 0x10;
        
        // Flag 5
        if (invisible) f5 = 0x20;

        // Flag 6
        if (glowing) f6 = 0x40;

        // Flag 7
        if (elytraEnabled) f7 = 0x80;

        byte value = (byte) (f0 | f1 | f2 | f3 | f4 | f5 | f6 | f7);

        NPCMain.instance.getLogger().info("Entity Meta Byte: "+ value);

        return value;
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

    public void setElytraEnabled(boolean value) {
        this.elytraEnabled = value;
    }

    public void setShivering(boolean value) {
        this.shivering = value;
    }

    public void setCollisionEnabled(boolean value) {
        this.collisionEnabled = value;
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
        return this.collisionEnabled;
    }
}
