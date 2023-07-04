package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nonnull;

import io.github.scroojalix.npcmanager.npc.meta.enums.ActiveHand;
import io.github.scroojalix.npcmanager.npc.meta.enums.MetaColor;
import io.github.scroojalix.npcmanager.npc.meta.enums.Pose;

public abstract class MetaFields<T> {

    public static final MetaFields<Pose> POSE = new MetaFields<Pose>("Pose", MetaIndex.POSE) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Pose value) {
            info.setPose(value);
        }
    };
    
    public static final MetaFields<Integer> POTION_COLOR = new MetaFields<Integer>("ParticleColor", MetaIndex.Living.POTION_EFFECT_COLOR) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setPotionEffectColor(value);
        }
    };
    
    public static final MetaFields<Integer> ARROWS = new MetaFields<Integer>("NumOfArrows", MetaIndex.Living.ARROWS) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setArrows(value);
        }
    };
    
    public static final MetaFields<Integer> STINGERS = new MetaFields<Integer>("NumOfStingers", MetaIndex.Living.STINGERS) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setStingers(value);
        }
    };

    public static final MetaFields<ActiveHand> ACTIVE_HAND = new MetaFields<ActiveHand>("ActiveHand", MetaIndex.Living.HAND_STATE) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull ActiveHand value) {
            info.setActiveHand(value);
        }
    };
    
    public static final MetaFields<MetaColor> GLOW_COLOR = new MetaFields<MetaColor>("GlowColor", null) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull MetaColor value) {
            info.setGlowColor(value);
        }
    };

    private final String name;
    private final MetaIndexInterface key;

    public MetaFields(String name, MetaIndexInterface key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        if (key == null) return -1;
        return MetaIndex.getIndex(key);
    }

    public abstract void setValue(@Nonnull NPCMetaInfo info, @Nonnull T value);
}
