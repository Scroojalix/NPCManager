package io.github.scroojalix.npcmanager.common.dependencies;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

import io.github.scroojalix.npcmanager.common.storage.StorageType;

public class DependencyRegistry {

    private static final SetMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES = ImmutableSetMultimap.<StorageType, Dependency>builder()
    .putAll(StorageType.MONGODB, Dependency.MONGODB_DRIVER)
    .putAll(StorageType.TOML, Dependency.TOML4J)
    .build();

    public static Set<Dependency> resolveStorageDependencies(StorageType storageType) {
        return STORAGE_DEPENDENCIES.get(storageType);
    }

    public static Set<Dependency> getGlobalDependencies() {
        Set<Dependency> dependencies = new HashSet<Dependency>();
        dependencies.add(Dependency.ASM);
        dependencies.add(Dependency.ASM_COMMONS);
        dependencies.add(Dependency.JAR_RELOCATOR);
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
