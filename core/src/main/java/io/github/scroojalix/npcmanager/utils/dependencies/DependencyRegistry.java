package io.github.scroojalix.npcmanager.utils.dependencies;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

import io.github.scroojalix.npcmanager.utils.storage.StorageType;

public class DependencyRegistry {

    private static final SetMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES = ImmutableSetMultimap.<StorageType, Dependency>builder()
    .putAll(StorageType.MONGODB, Dependency.MONGODB_DRIVER)
    .build();

    public static Set<Dependency> resolveStorageDependencies(StorageType storageType) {
        return STORAGE_DEPENDENCIES.get(storageType);
    }

    public static Set<Dependency> getGlobalDependencies() {
        Set<Dependency> dependencies = new HashSet<Dependency>();
        dependencies.add(Dependency.ASM);
        return dependencies;
    }

    public static boolean shouldAutoLoad(Dependency dependency) {
        switch (dependency) {
            // all used within 'isolated' classloaders, and are therefore not relocated.
            case ASM:
            case ASM_COMMONS:
            case JAR_RELOCATOR:
                return false;
            default:
                return true;
        }
    }
    
}
