package io.github.scroojalix.npcmanager.npc.skin;

import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

import org.bukkit.ChatColor;

public class NPCSkinLayers implements Serialisable {

    @Expose
    private boolean cape;
    @Expose
    private boolean jacket;
    @Expose
    private boolean leftSleeve;
    @Expose
    private boolean rightSleeve;
    @Expose
    private boolean leftLeg;
    @Expose
    private boolean rightLeg;
    @Expose
    private boolean hat;

    public NPCSkinLayers() {
        cape = true;
        jacket = true;
        leftSleeve = true;
        rightSleeve = true;
        leftLeg = true;
        rightLeg = true;
        hat = true;
    }

    public String getCurrentConfiguration() {
        SkinLayer[] skinParts = SkinLayer.values();
        boolean[] values = getBoolArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < skinParts.length; i++) {
            builder.append(ChatColor.GOLD+skinParts[i].label.replace("--","")+": "+ChatColor.WHITE+values[i]);
            if (i < skinParts.length - 1) builder.append(ChatColor.GOLD+", ");
        }
        return builder.toString();
    }

    public byte getDisplayedSkinParts() {
        byte bitSize = 1;
        byte result = 0;
        boolean[] bools = getBoolArray();
        for (int i = 0; i < bools.length; i++) {
            if (bools[i]) result += bitSize;
            bitSize *= 2;
        }
        return result;
    }

    // TODO maybe a better way of implementing this?
    // Maybe move all meta index getters to own class
    public static int getSkinLayersByteIndex() {
        switch(NPCMain.serverVersion) {
            case v1_8_R2:
            case v1_8_R3:
                return 10;
            case v1_9_R1:
            case v1_9_R2:
                return 12;
            case v1_10_R1:
            case v1_11_R1:
            case v1_12_R1:
            case v1_13_R1:
            case v1_13_R2:
                return 13;
            case v1_14_R1:
                return 15;
            case v1_15_R1:
            case v1_16_R1:
            case v1_16_R2:
            case v1_16_R3:
                return 16;
            case v1_17_R1:
            case v1_18_R1:
            case v1_18_R2:
            case v1_19_R1:
            case v1_19_R2:
            case v1_19_R3:
            case v1_20_R1:
                return 17;
            default:
                // This should never get called. It is only here to alert me if I
                // forget to update this.
                throw new IllegalArgumentException("Unknown Enum Value for ServerVersion: " + NPCMain.serverVersion.toString());
        }
    }

    public boolean[] getBoolArray() {
        return new boolean[] {cape, jacket, leftSleeve, rightSleeve, leftLeg, rightLeg, hat};
    }

    public boolean usingCape() {
        return this.cape;
    }

    public void setCape(boolean cape) {
        this.cape = cape;
    }

    public boolean usingJacket() {
        return this.jacket;
    }

    public void setJacket(boolean jacket) {
        this.jacket = jacket;
    }

    public boolean usingLeftSleeve() {
        return this.leftSleeve;
    }

    public void setLeftSleeve(boolean leftSleeve) {
        this.leftSleeve = leftSleeve;
    }

    public boolean usingRightSleeve() {
        return this.rightSleeve;
    }

    public void setRightSleeve(boolean rightSleeve) {
        this.rightSleeve = rightSleeve;
    }

    public boolean usingLeftLeg() {
        return this.leftLeg;
    }

    public void setLeftLeg(boolean leftLeg) {
        this.leftLeg = leftLeg;
    }

    public boolean usingRightLeg() {
        return this.rightLeg;
    }

    public void setRightLeg(boolean rightLeg) {
        this.rightLeg = rightLeg;
    }

    public boolean usingHat() {
        return this.hat;
    }

    public void setHat(boolean hat) {
        this.hat = hat;
    }
}
