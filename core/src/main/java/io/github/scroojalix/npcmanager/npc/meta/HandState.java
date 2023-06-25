package io.github.scroojalix.npcmanager.npc.meta;

import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

public class HandState implements Serialisable {

        @Expose
        private boolean active;
        @Expose
        private Hand hand;
        @Expose
        private boolean isRiptideSpinAttack;

        HandState() {}

        public HandState(boolean active, Hand hand, boolean riptide) {
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