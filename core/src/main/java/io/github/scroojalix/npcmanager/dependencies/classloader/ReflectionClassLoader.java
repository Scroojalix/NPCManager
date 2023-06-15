package io.github.scroojalix.npcmanager.dependencies.classloader;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import io.github.scroojalix.npcmanager.NPCMain;

public class ReflectionClassLoader {

    @SuppressWarnings("Guava")
    private static final Supplier<URLClassLoaderAccess> URL_INJECTOR = Suppliers.memoize(
            () -> URLClassLoaderAccess.create((URLClassLoader) NPCMain.class.getClassLoader()));

    public static void addJarToClasspath(Path file) {
        try {
            URL_INJECTOR.get().addURL(file.toUri().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load a dependency.", e);
        }
    }
}
