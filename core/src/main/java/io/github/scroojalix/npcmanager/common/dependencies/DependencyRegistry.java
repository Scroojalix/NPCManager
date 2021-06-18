package io.github.scroojalix.npcmanager.common.dependencies;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import io.github.scroojalix.npcmanager.common.storage.StorageType;

public class DependencyRegistry {

    private static final ListMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES = ImmutableListMultimap.<StorageType, Dependency>builder()
    .putAll(StorageType.MONGODB, Dependency.MONGODB_DRIVER)
    .build();

    public static Set<Dependency> resolveStorageDependencies(StorageType storageType) {
        Set<Dependency> dependencies = new LinkedHashSet<>();
        dependencies.addAll(STORAGE_DEPENDENCIES.get(storageType));
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
