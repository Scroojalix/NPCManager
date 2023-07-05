package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.scroojalix.npcmanager.npc.meta.enums.ActiveHand;
import io.github.scroojalix.npcmanager.npc.meta.enums.MetaColor;
import io.github.scroojalix.npcmanager.npc.meta.enums.Pose;

public abstract class MetaField<T> {

    public static final @Nonnull MetaField<Pose> POSE = new MetaField<Pose>("Pose", MetaIndex.POSE, Pose.class) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Pose value) {
            info.setPose(value);
        }
    };
    
    public static final @Nonnull MetaField<Integer> PARTICLE_COLOR = new MetaField<Integer>("ParticleColor", MetaIndex.Living.POTION_EFFECT_COLOR, int.class) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setParticleColor(value);
        }
    };
    
    public static final @Nonnull MetaField<Integer> ARROWS = new MetaField<Integer>("NumOfArrows", MetaIndex.Living.ARROWS, int.class) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setArrows(value);
        }
    };
    
    public static final @Nonnull MetaField<Integer> STINGERS = new MetaField<Integer>("NumOfStingers", MetaIndex.Living.STINGERS, int.class) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull Integer value) {
            info.setStingers(value);
        }
    };

    public static final @Nonnull MetaField<ActiveHand> ACTIVE_HAND = new MetaField<ActiveHand>("ActiveHand", MetaIndex.Living.HAND_STATE, ActiveHand.class) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull ActiveHand value) {
            info.setActiveHand(value);
        }
    };
    
    public static final @Nonnull MetaField<MetaColor> GLOW_COLOR = new MetaField<MetaColor>("GlowColor", null, MetaColor.class) {
        @Override
        public void setValue(@Nonnull NPCMetaInfo info, @Nonnull MetaColor value) {
            info.setGlowColor(value);
        }
    };

    public final String fieldName;
    public final Class<T> valueClass;

    private final MetaIndexInterface key;

    public MetaField(String fieldName, MetaIndexInterface indexKey, Class<T> valueClass) {
        this.fieldName = fieldName;
        this.key = indexKey;
        this.valueClass = valueClass;
    }

    public int getIndex() {
        if (key == null) return -1;
        return MetaIndex.getIndex(key);
    }

    public abstract void setValue(@Nonnull NPCMetaInfo info, @Nonnull T value);

    public static MetaField<?>[] values() {
        return new MetaField<?>[] {
            POSE, PARTICLE_COLOR, ARROWS, STINGERS, ACTIVE_HAND, GLOW_COLOR
        };
    }

    public static @Nullable MetaField<?> fromName(String fieldName) {
        for (MetaField<?> field : values()) {
            if (field.fieldName.equals(fieldName)) return field;
        }
        return null;
    }
}
