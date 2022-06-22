package PZHook_server.config;

import net.uptheinter.interceptify.interfaces.StartupConfig;
import net.uptheinter.interceptify.internal.RuntimeHook;
import net.uptheinter.interceptify.util.JarFiles;
import net.uptheinter.interceptify.util.Util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class HookConfig implements StartupConfig {
    private final Set<ModData> mods = initMods();

    public HookConfig() throws ClassNotFoundException {
    }

    private static ModData tryAddModInfo(Path javaDir) {
        System.out.println("Trying to add mod data from path: " + javaDir);
        var modInfo = javaDir.getParent().resolve("mod.info");
        if (!Files.isRegularFile(modInfo))
            return null;
        List<String> lines;
        try {
            lines = Files.readAllLines(modInfo);
        } catch (IOException e) {
            Util.DebugError(e);
            return null;
        }
        var name = lines.stream()
                .filter(line -> line.startsWith("name"))
                .map(line -> line.substring(line.indexOf('=') + 1))
                .findAny().orElse(null);
        if (name == null)
            return null;
        return new ModData(name, javaDir);
    }

    private Set<ModData> initMods() {
        final var mods = new HashSet<ModData>();
        try {
            findmod(Paths.get(Paths.get("").toAbsolutePath() + "/Zomboid/mods"))
                    .forEach(mods::add);
        } catch (IOException ignored) {}
        try {
            findmod(Paths.get(System.getProperty("user.home") + "/Zomboid/mods"))
                    .forEach(mods::add);
        } catch (IOException ignored) {}
        return mods;
    }

    private Stream<ModData> findmod(Path path) throws IOException {
        return Files.walk(path, 1)
                .filter(p -> !p.equals(path))
                .filter(Files::isDirectory)
                .flatMap(p -> Util.walk(p, 1)
                        .filter(Files::isDirectory)
                        .filter(o -> !o.equals(p)))
                .filter(p -> p.endsWith("java"))
                .map(HookConfig::tryAddModInfo)
                .filter(Objects::nonNull);
    }

    @Override
    public Consumer<String[]> getRealMain() {
        try {
            var main = Class.forName("zombie.network.GameServer")
                    .getDeclaredMethod("main", String[].class);
            return args -> {
                try {
                    main.invoke(null, (Object) args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            };
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Couldn't find the main game class!");
        }
    }

    @Override
    public List<URL> getClasspaths() {
        try {
            var allJarFiles = StartupConfig.super.getClasspaths();
            Files.walk(Path.of(""), 1)
                    .filter(f -> f.toFile().getName().endsWith(".jar"))
                    .map(Path::toAbsolutePath)
                    .map(Util::toURL)
                    .forEach(allJarFiles::add);
            allJarFiles.add(Util.toURL(Paths.get("").toAbsolutePath()));
            allJarFiles.add(RuntimeHook.class.getProtectionDomain().getCodeSource().getLocation());
            return allJarFiles;
        } catch (IOException e) {
            Util.DebugError(e);
        }
        return new ArrayList<>();
    }

    public Set<ModData> getMods() {
        return mods;
    }

    @Override
    public JarFiles getJarFilesToInject() {

        var allFiles = new JarFiles();
        mods.stream()
                .filter(ModData::isEnabled)
                .map(ModData::getJarDir)
                .forEach(allFiles::addFromDirectory);
        return allFiles;
    }
}
