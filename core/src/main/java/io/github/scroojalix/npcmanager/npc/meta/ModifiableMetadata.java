package io.github.scroojalix.npcmanager.npc.meta;

public enum ModifiableMetadata {

    POSE("Pose", MetaIndex.POSE, Pose.class),
    POTION_COLOR("ParticleColor", MetaIndex.Living.POTION_EFFECT_COLOR, MetaColor.class),
    ARROWS("NumOfArrows", MetaIndex.Living.ARROWS, Integer.class),
    STINGERS("NumOfStingers", MetaIndex.Living.STINGERS, Integer.class),   
    
    GLOW_COLOR("GlowColor", null, MetaColor.class);

    private final String name;
    private final MetadataType key;
    private final Class<?> valueClass;

    ModifiableMetadata(String name, MetadataType key, Class<?> valueClass) {
        this.name = name;
        this.key = key;
        this.valueClass = valueClass;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        if (key == null) return -1;
        return MetaIndex.getIndex(key);
    }

    public Class<?> getValueClass() {
        return valueClass;
    }
}
