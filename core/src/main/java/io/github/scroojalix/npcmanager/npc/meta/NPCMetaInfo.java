package io.github.scroojalix.npcmanager.npc.meta;

import java.util.HashSet;

import javax.annotation.Nonnull;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.storage.misc.Serialisable;
import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

/**
 * Store all metadata for an NPC. Handles per version metadata format.
 * @author Scroojalix
 * @see https://wiki.vg/Entity_metadata
 */
public class NPCMetaInfo implements Serialisable {

    /**
     * This field will only have an affect on 1.14+ servers<p>
     * Index = 6
     */
    @Expose
    private @Nonnull Pose pose;

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
     * List of active flags.
     * @see Flag
     */
    @Expose
    private HashSet<Flag> activeFlags;

    /**
     * Initialise a new NPCMetaInfo object. Used when clearing an NPC's meta.
     */
    public NPCMetaInfo() {
        this.pose = Pose.STANDING;
        this.handState = new HandState(false, Hand.MAIN_HAND, false);
        this.glowColor = GlowColor.WHITE;
        this.activeFlags = new HashSet<>();
    }

    /**
     * Return the currently selected pose for this NPC
     * @return this NPC's pose.
     */
    public Pose getPose() {
        return this.pose;
    }

    /**
     * Set this NPC's pose
     * @param pose the new pose to set to.
     */
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
        if (hasFlag(Flag.ON_FIRE)) f0 = 0x01;

        // Flag 1
        if (pose == Pose.CROUCHING) f1 = 0x02;

        // Flag 2
        if (pose == Pose.SITTING) f2 = 0x04;

        // Flag 3
        if (hasFlag(Flag.SPRINTING)) f3 = 0x08;

        // Flag 4
        if ((handState.isActive())
            || (pose == Pose.SWIMMING && ServerVersion.v1_13_R1.atOrAbove()))
            f4 = 0x10;
        
        // Flag 5
        if (hasFlag(Flag.INVISIBLE)) f5 = 0x20;

        // Flag 6
        if (hasFlag(Flag.GLOWING)) f6 = 0x40;

        // Flag 7
        if (hasFlag(Flag.ELYTRA_ENABLED)) f7 = 0x80;

        return (byte) (f0 | f1 | f2 | f3 | f4 | f5 | f6 | f7);
    }

    public HandState getHandState() {
        return this.handState;
    }

    public GlowColor getGlowColor() {
        return glowColor;
    }

    public void setGlowColor(@Nonnull GlowColor glowColor) {
        this.glowColor = glowColor;
    }

    public void addFlag(Flag flag) {
        this.activeFlags.add(flag);
    }

    public boolean hasFlag(Flag flag) {
        return activeFlags.contains(flag);
    }

    public void removeFlag(Flag flag) {
        activeFlags.remove(flag);
    }
}
