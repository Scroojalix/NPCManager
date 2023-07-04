package io.github.scroojalix.npcmanager.npc.meta;

import java.util.HashSet;

import javax.annotation.Nonnull;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.npc.meta.enums.ActiveHand;
import io.github.scroojalix.npcmanager.npc.meta.enums.Flag;
import io.github.scroojalix.npcmanager.npc.meta.enums.MetaColor;
import io.github.scroojalix.npcmanager.npc.meta.enums.Pose;
import io.github.scroojalix.npcmanager.npc.skin.NPCSkinLayers;
import io.github.scroojalix.npcmanager.storage.misc.Serialisable;
import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

/**
 * Store all metadata for an NPC. Handles per version metadata format.
 * All the information stored in this class is used to generate the
 * Entity Metadata Packet for spawning the NPC.
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
     * Only used in 1.9+ servers.
     */
    @Expose
    private @Nonnull ActiveHand activeHand;

    /**
     * Sent as part of the scoreboard team packet<p>
     */
    @Expose
    private int potionEffectColor;

    /**
     * Sent as part of the scoreboard team packet<p>
     * Has no effect on 1.8 servers
     */
    @Expose
    private @Nonnull MetaColor glowColor;

    /**
     * Number of arrows stuck in NPC.
     */
    @Expose
    private int arrows;

    /**
     * Number of stingers stuck in NPC.
     * Since 1.15
     */
    @Expose
    private int stingers;

    /**
     * List of active flags.
     * @see Flag
     */
    @Expose
    private @Nonnull HashSet<Flag> activeFlags;

    /**
     * Container for the NPC's currently active skin layers
     */
    @Expose
    private NPCSkinLayers skinLayers;

    /**
     * Initialise a new NPCMetaInfo object. Used when clearing an NPC's meta.
     */
    public NPCMetaInfo() {
        this.pose = Pose.STANDING;
        this.activeHand = ActiveHand.NONE;
        this.glowColor = MetaColor.NONE;
        this.activeFlags = new HashSet<>();
    }

    // Byte getters

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
        if ((activeHand != ActiveHand.NONE)
            || (pose == Pose.SWIMMING && ServerVersion.v1_13_R1.atOrAbove()))
            f4 = 0x10;
        
        // Flag 5
        if (hasFlag(Flag.INVISIBLE)) f5 = 0x20;

        // Flag 6
        if (glowColor != MetaColor.NONE) f6 = 0x40;

        // Flag 7
        if (hasFlag(Flag.ELYTRA_ENABLED)) f7 = 0x80;

        return (byte) (f0 | f1 | f2 | f3 | f4 | f5 | f6 | f7);
    }

    /**
     * Get the byte flag for this HandState
     * @return return byte flag for use in NPC data watcher.
     */
    public byte getHandStateFlag() {
        int flag1 = activeHand != ActiveHand.NONE ? 0x1 : 0;
        int flag2 = activeHand.getNMSHand() == Hand.MAIN_HAND ? 0 : 0x2;
        int flag3 = hasFlag(Flag.RIPTIDE_SPIN) ? 0x4 : 0;
        return (byte)(flag1 | flag2 | flag3);
    }

    public byte getSkinLayersByte() {
        if (skinLayers != null) {
            return skinLayers.getDisplayedSkinParts();
        }
        return 127;
    }

    public <T> void setFieldValue(@Nonnull MetaField<T> field, @Nonnull T value) {
        field.setValue(this, value);
    }

    // GETTERS

    public NPCSkinLayers getSkinLayers() {
        return this.skinLayers;
    }

    public void setSkinLayers(NPCSkinLayers skinLayers) {
        this.skinLayers = skinLayers;
    }

    public int getPotionEffectColor() {
        return potionEffectColor;
    }

    /**
     * Return the currently selected pose for this NPC
     * @return this NPC's pose.
     */
    public Pose getPose() {
        return this.pose;
    }

    public MetaColor getGlowColor() {
        return glowColor;
    }

    public int getArrows() {
        return arrows;
    }

    public int getStingers() {
        return stingers;
    }

    // SETTERS

    /**
     * Set this NPC's pose
     * @param pose the new pose to set to.
     */
    public void setPose(@Nonnull Pose pose) {
        this.pose = pose;
    }

    public void setPotionEffectColor(int potionEffectColor) {
        this.potionEffectColor = potionEffectColor;
    }

    public void setGlowColor(@Nonnull MetaColor glowColor) {
        this.glowColor = glowColor;
    }

    public void setArrows(int arrows) {
        this.arrows = arrows;
    }

    public void setStingers(int stingers) {
        this.stingers = stingers;
    }

    public void setActiveHand(@Nonnull ActiveHand hand) {
        this.activeHand = hand;
    }

    // FLAGS

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
