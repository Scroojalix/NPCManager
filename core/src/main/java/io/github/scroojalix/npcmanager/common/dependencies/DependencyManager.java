package io.github.scroojalix.npcmanager.common.dependencies;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.dependencies.classloader.IsolatedClassLoader;
import io.github.scroojalix.npcmanager.common.dependencies.classloader.ReflectionClassLoader;
import io.github.scroojalix.npcmanager.common.dependencies.relocation.Relocation;
import io.github.scroojalix.npcmanager.common.dependencies.relocation.RelocationHandler;
import io.github.scroojalix.npcmanager.common.storage.StorageType;

public class DependencyManager {

    private final NPCMain main;
    private final String url;

    private final Map<ImmutableSet<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
    private final EnumMap<Dependency, Path> loaded = new EnumMap<>(Dependency.class);
    private RelocationHandler relocationHandler = null;
    private ReflectionClassLoader pluginClassLoader;
    
    public DependencyManager(NPCMain main) {
        this.main = main;
        url = "https://repo1.maven.org/maven2/";
        this.pluginClassLoader = new ReflectionClassLoader(main);
        loadDependencies(DependencyRegistry.getGlobalDependencies());
    }

    public void loadStorageDependencies(StorageType type) {
        loadDependencies(DependencyRegistry.resolveStorageDependencies(type));
    }

    public void loadDependencies(Set<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            try {
                loadDependency(dependency);
            } catch (Exception e) {
                this.main.getLogger().log(Level.SEVERE, "Unable to load dependency " + dependency.name() + ".", e);
            }
        }
    }

    private void loadDependency(Dependency dependency) throws Exception {
        if (this.loaded.containsKey(dependency)) {
            return;
        }
        Path file = remapDependency(dependency, downloadDependency(dependency));

        this.loaded.put(dependency, file);

        if (DependencyRegistry.shouldAutoLoad(dependency)) {
            this.pluginClassLoader.addJarToClasspath(file);
        }
    }

    private Path remapDependency(Dependency dependency, Path normalFile) throws Exception {
        List<Relocation> rules = new ArrayList<>(dependency.getRelocations());

        if (rules.isEmpty()) {
            return normalFile;
        }

        Path remappedFile = this.getCacheDirectory().resolve(dependency.getFileName() + "-remapped.jar");

        // if the remapped source exists already, just use that.
        if (Files.exists(remappedFile)) {
            return remappedFile;
        }

        getRelocationHandler().remap(normalFile, remappedFile, rules);
        return remappedFile;
    }

    private synchronized RelocationHandler getRelocationHandler() {
        if (this.relocationHandler == null) {
            this.relocationHandler = new RelocationHandler(this);
        }
        return this.relocationHandler;
    }

    public IsolatedClassLoader obtainClassLoaderWith(Set<Dependency> dependencies) {
        ImmutableSet<Dependency> set = ImmutableSet.copyOf(dependencies);

        for (Dependency dependency : dependencies) {
            if (!this.loaded.containsKey(dependency)) {
                throw new IllegalStateException("Dependency " + dependency + " is not loaded.");
            }
        }

        synchronized (this.loaders) {
            IsolatedClassLoader classLoader = this.loaders.get(set);
            if (classLoader != null) {
                return classLoader;
            }

            URL[] urls = set.stream()
                    .map(this.loaded::get)
                    .map(file -> {
                        try {
                            return file.toUri().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);

            classLoader = new IsolatedClassLoader(urls);
            this.loaders.put(set, classLoader);
            return classLoader;
        }
    }

    /**
     * Attempt to download a dependency
     * @param dependency The dependency to download.
     * @return The path to the dependency
     * @throws DependencyDownloadException if something goes wrong when attemting to download the dependency.
     */
    private Path downloadDependency(Dependency dependency) throws DependencyDownloadException {
        Path file = this.getCacheDirectory().resolve(dependency.getFileName() + ".jar");
        if (Files.exists(file)) {
            return file;
        }
        try {
            download(dependency, file);
            return file;
        } catch (DependencyDownloadException e) {
            throw new DependencyDownloadException(e);
        }
    }

    private void download(Dependency dependency, Path file) throws DependencyDownloadException {
        try {
            Files.write(file, download(dependency));
        } catch (IOException e) {
            throw new DependencyDownloadException(e);
        }
    }
    
    private byte[] download(Dependency dependency) throws DependencyDownloadException {
        byte[] bytes = downloadRaw(dependency);
        byte[] hash = Dependency.createDigest().digest(bytes);
        if (!dependency.checksumMatches(hash)) {
            throw new DependencyDownloadException("Downloaded file had an invalid hash. " +
                    "Expected: " + Base64.getEncoder().encodeToString(dependency.getChecksum()) + " " +
                    "Actual: " + Base64.getEncoder().encodeToString(hash));
        }
        return bytes;
    }

    private byte[] downloadRaw(Dependency dependency) throws DependencyDownloadException {
        try {
            URLConnection connection = new URL(this.url + dependency.getMavenRepoPath()).openConnection();
            try (InputStream in = connection.getInputStream()) {
                byte[] bytes = ByteStreams.toByteArray(in);
                if (bytes.length == 0) {
                    throw new DependencyDownloadException("Empty stream");
                }
                return bytes;
            }
        } catch (Exception e) {
            throw new DependencyDownloadException(e);
        }
    }

    private Path getCacheDirectory() {
        Path cache = main.getDataFolder().toPath().toAbsolutePath().resolve("libs");
        if (Files.exists(cache) && (Files.isDirectory(cache) || Files.isSymbolicLink(cache))) {
            return cache;
        }
        try {
            Files.createDirectories(cache);
        } catch (FileAlreadyExistsException e) {
            //Ignore
        } catch (IOException e) {
            throw new RuntimeException("Unable to create libs directory", e);
        }
        return cache;
    }
}
