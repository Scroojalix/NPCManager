package io.github.scroojalix.npcmanager.npc.meta;

import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

/**
 * Use this class to get an index for formatting an NPC's Metadata.
 * IMPORTANT: DO NOT CHANGE THE ORDER OF THE ENUM CONSTANTS
 */
public enum MetaIndex implements MetaIndexInterface {

    // Entity Base
    BASE,
    AIR_TICKS,
    CUSTOM_NAME,
    CUSTOM_NAME_VISIBLE,
    SILENT,
    GRAVITY(ServerVersion.v1_10_R1),
    POSE(ServerVersion.v1_14_R1),
    POWDERED_SNOW(ServerVersion.v1_17_R1);

    private final ServerVersion versionAdded;

    MetaIndex() {
        this(ServerVersion.v1_8_R2);
    }

    MetaIndex(ServerVersion versionAdded) {
        this.versionAdded = versionAdded;
    }

    @Override
    public ServerVersion getVersionAdded() {
        return versionAdded;
    }

    public enum Living implements MetaIndexInterface {
        HAND_STATE, // Technically added in 1.9 but doesnt change indexes
        HEALTH,
        POTION_EFFECT_COLOR,
        POTION_EFFECT_AMBIENT,
        ARROWS,
        STINGERS(ServerVersion.v1_15_R1),// doubles as absorption for 1.15-1.16 servers
        BED_LOCATION(ServerVersion.v1_14_R1);

        private final ServerVersion versionAdded;

        Living() {
            this(ServerVersion.v1_8_R2);
        }

        Living(ServerVersion versionAdded) {
            this.versionAdded = versionAdded;
        }

        @Override
        public ServerVersion getVersionAdded() {
            return versionAdded;
        }

        @Override
        public MetaIndexInterface getParent() {
            return MetaIndex.POWDERED_SNOW;
        }

        public static enum Player implements MetaIndexInterface {
            ADDITIONAL_HEARTS(ServerVersion.v1_9_R1),
            SCORE(ServerVersion.v1_9_R1),
            SKIN_PARTS,
            MAIN_HAND,
            LEFT_SHOULDER(ServerVersion.v1_12_R1),
            RIGHT_SHOULDER(ServerVersion.v1_12_R1);
            
            private final ServerVersion versionAdded;

            Player() {
                this(ServerVersion.v1_8_R2);
            }

            Player(ServerVersion versionAdded) {
                this.versionAdded = versionAdded;
            }

            @Override
            public ServerVersion getVersionAdded() {
                return versionAdded;
            }

            @Override
            public MetaIndexInterface getParent() {
                return MetaIndex.Living.BED_LOCATION;
            }
        }
    
        public static enum ArmorStand implements MetaIndexInterface {
            META,
            HEAD,
            BODY,
            LEFT_ARM,
            RIGHT_ARM,
            LEFT_LEG,
            RIGHT_LEG;

            @Override
            public MetaIndexInterface getParent() {
                return MetaIndex.Living.BED_LOCATION;
            }
        }
    }

    /**
     * Gets the metadata index for a specified Metadata value
     * @param metaType The metadata index to search for.
     * {@link MetaIndex} is the base class
     * @return an integer representing the index of this MetadataType.
     */
    public static int getIndex(MetaIndexInterface metaType) {
        int index = 0;
        
        MetaIndexInterface parent = metaType.getParent();
        if (parent != null) {
            index += getIndex(parent) + (parent.getVersionAdded().atOrAbove() ? 1 : 0);
        }
        
        for (MetaIndexInterface value : metaType.getClass().getEnumConstants()) {
            if (value == metaType) break;
            if (value.getVersionAdded().atOrAbove()) {
                index++;
            }
        }
        return index;
    }
}
