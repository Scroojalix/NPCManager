package io.github.scroojalix.npcmanager.common.dependencies.classloader;

import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import io.github.scroojalix.npcmanager.NPCMain;

public class ReflectionClassLoader {

    @SuppressWarnings("Guava")
    private static final Supplier<Method> ADD_URL_METHOD = Suppliers.memoize(() -> {
        // If on Java 9+, open the URLClassLoader module to this module
        // so we can access its API via reflection without producing a warning.
        try {
            openUrlClassLoaderModule();
        } catch (Exception e) {
            e.printStackTrace();
            // ignore exception - will throw on Java 8 since the Module classes don't exist
        }
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

    public static void addJarToClasspath(Path file) {
        try {
            URL_INJECTOR.get().addURL(file.toUri().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load a dependency.", e);
        }
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private static void openUrlClassLoaderModule() throws Exception {
        Class<?> moduleClass = Class.forName("java.lang.Module");
        Method getModuleMethod = Class.class.getMethod("getModule");
        Method addOpensMethod = moduleClass.getMethod("addOpens", String.class, moduleClass);

        Object urlClassLoaderModule = getModuleMethod.invoke(URLClassLoader.class);
        Object thisModule = getModuleMethod.invoke(ReflectionClassLoader.class);

        //TODO get base module
        Object baseModule = getModuleMethod.invoke(Class.forName("java.base"));

        NPCMain.instance.getLogger().info(thisModule.toString());

        addOpensMethod.invoke(baseModule, "java.net", thisModule);

        addOpensMethod.invoke(urlClassLoaderModule, URLClassLoader.class.getPackage().getName(), thisModule);
    }
}
