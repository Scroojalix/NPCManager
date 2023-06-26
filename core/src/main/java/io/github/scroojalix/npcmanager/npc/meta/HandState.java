package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nonnull;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

/**
 * Store information on an NPC's hand state.<p>
 * (ie. Blocking, active hand, riptide spin attack)
 * @see https://wiki.vg/Entity_metadata#Living_Entity
 */
public class HandState implements Serialisable {

    
    @Expose
    private boolean active;
    @Expose
    private @Nonnull Hand hand;
    @Expose
    private boolean isRiptideSpinAttack;

    HandState() {
        this.hand = Hand.MAIN_HAND;
    }

    public HandState(boolean active, @Nonnull Hand hand, boolean riptide) {
        this.active = active;
        this.hand = hand;
        this.isRiptideSpinAttack = riptide;
    }

    public void set(boolean active, @Nonnull Hand hand) {
        this.active = active;
        this.hand = hand;
    }

    public void setIsRiptideSpin(boolean isRiptideSpinAttack) {
        this.isRiptideSpinAttack = isRiptideSpinAttack;
    }

    /**
     * Get the index of this field in the data watcher.<p>
     * NOTE: this is not available in 1.8 servers.
     * Instead use {@code blocking} flag in {@see NPCMetaInfo}
     * @return index of hand state metadata in NPC data watcher.
     */
    public static int getIndex() {
        switch (NPCMain.serverVersion) {
            case v1_9_R1:
            case v1_9_R2:
                return 5;
            case v1_10_R1:
            case v1_11_R1:
            case v1_12_R1:
            case v1_13_R1:
            case v1_13_R2:
                return 6;
            case v1_14_R1:
            case v1_15_R1:
            case v1_16_R1:
            case v1_16_R2:
            case v1_16_R3:
                return 7;
            case v1_17_R1:
            case v1_18_R1:
            case v1_18_R2:
            case v1_19_R1:
            case v1_19_R2:
            case v1_19_R3:
            case v1_20_R1:
                return 8;
            default:
                throw new IllegalArgumentException("That version does not have hand state meta");
        }
    }

    /**
     * Get the byte flag for this HandState
     * @return return byte flag for use in NPC data watcher.
     */
    public byte getByteFlag() {
        int flag1 = active ? 0x1 : 0;
        int flag2 = hand.ordinal() == 0 ? 0 : 0x2;
        int flag3 = isRiptideSpinAttack ? 0x4 : 0;
        return (byte)(flag1 | flag2 | flag3);
    }
}