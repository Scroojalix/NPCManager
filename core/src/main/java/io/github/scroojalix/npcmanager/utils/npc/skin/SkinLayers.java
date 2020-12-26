package io.github.scroojalix.npcmanager.utils.npc.skin;

import com.google.gson.annotations.Expose;

public class SkinLayers {

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

    public SkinLayers() {
        cape = true;
        jacket = true;
        leftSleeve = true;
        rightSleeve = true;
        leftLeg = true;
        rightLeg = true;
        hat = true;
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

    public static String[] skinParts = new String[] {"cape", "jacket", "leftSleeve", "rightSleeve", "leftLeg", "rightLeg", "hat"};

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
