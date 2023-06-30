package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nonnull;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

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

    public boolean isActive() {
        return active;
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