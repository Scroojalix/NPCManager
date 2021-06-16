package io.github.scroojalix.npcmanager.utils.dependencies.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import io.github.scroojalix.npcmanager.NPCMain;

public class ReflectionClassLoader {

    @SuppressWarnings("Guava")
    private static final Supplier<Method> ADD_URL_METHOD = Suppliers.memoize(() -> {
        try {
            Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);
            return addUrlMethod;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    });
    private final URLClassLoader classLoader;

    public ReflectionClassLoader(NPCMain main) throws IllegalStateException {
        ClassLoader classLoader = main.getClass().getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            this.classLoader = (URLClassLoader) classLoader;
        } else {
            throw new IllegalStateException("ClassLoader is not instance of URLClassLoader");
        }
    }

    public void addJarToClasspath(Path file) {
        try {
            ADD_URL_METHOD.get().invoke(this.classLoader, file.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
