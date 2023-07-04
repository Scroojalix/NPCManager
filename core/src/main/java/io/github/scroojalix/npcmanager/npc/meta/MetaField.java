package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nonnull;

import io.github.scroojalix.npcmanager.npc.meta.enums.ActiveHand;
import io.github.scroojalix.npcmanager.npc.meta.enums.MetaColor;
import io.github.scroojalix.npcmanager.npc.meta.enums.Pose;

public abstract class MetaField<T> {

    public static final MetaField<Pose> POSE = new MetaField<Pose>("Pose", MetaIndex.POSE) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Pose value) {
            info.setPose(value);
        }
    };
    
    public static final MetaField<Integer> POTION_COLOR = new MetaField<Integer>("ParticleColor", MetaIndex.Living.POTION_EFFECT_COLOR) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setPotionEffectColor(value);
        }
    };
    
    public static final MetaField<Integer> ARROWS = new MetaField<Integer>("NumOfArrows", MetaIndex.Living.ARROWS) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setArrows(value);
        }
    };
    
    public static final MetaField<Integer> STINGERS = new MetaField<Integer>("NumOfStingers", MetaIndex.Living.STINGERS) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setStingers(value);
        }
    };

    public static final MetaField<ActiveHand> ACTIVE_HAND = new MetaField<ActiveHand>("ActiveHand", MetaIndex.Living.HAND_STATE) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull ActiveHand value) {
            info.setActiveHand(value);
        }
    };
    
    public static final MetaField<MetaColor> GLOW_COLOR = new MetaField<MetaColor>("GlowColor", null) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull MetaColor value) {
            info.setGlowColor(value);
        }
    };

    private final String name;
    private final MetaIndexInterface key;

    public MetaField(String name, MetaIndexInterface key) {
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
