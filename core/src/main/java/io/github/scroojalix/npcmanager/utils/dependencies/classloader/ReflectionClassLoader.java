package io.github.scroojalix.npcmanager.utils.dependencies.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

import io.github.scroojalix.npcmanager.NPCMain;

public class ReflectionClassLoader {

    private static final Method ADD_URL_METHOD;

    static {
        // If on Java 9+, open the URLClassLoader module to this module
        // so we can access its API via reflection without producing a warning.
        try {
            openUrlClassLoaderModule();
        } catch (Throwable e) {
            // ignore exception - will throw on Java 8 since the Module classes don't exist
        }

        // Get the protected 'addURL' method on URLClassLoader and set it to accessible.
        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

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
            ADD_URL_METHOD.invoke(this.classLoader, file.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private static void openUrlClassLoaderModule() throws Exception {
        Class<?> moduleClass = Class.forName("java.lang.Module");
        Method getModuleMethod = Class.class.getMethod("getModule");
        Method addOpensMethod = moduleClass.getMethod("addOpens", String.class, moduleClass);

        Object urlClassLoaderModule = getModuleMethod.invoke(URLClassLoader.class);
        Object thisModule = getModuleMethod.invoke(ReflectionClassLoader.class);

        addOpensMethod.invoke(urlClassLoaderModule, URLClassLoader.class.getPackage().getName(), thisModule);
    }
}
