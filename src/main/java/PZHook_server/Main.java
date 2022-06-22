package PZHook_server;

import PZHook_server.config.HookConfig;
import net.uptheinter.interceptify.EntryPoint;

import java.lang.instrument.Instrumentation;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    private static HookConfig cfg;

    static {
        try {
            cfg = new HookConfig();
        } catch (ClassNotFoundException e) {
            // not running from game dir, no problem.
        }
    }

    public static void main(String[] args) throws Exception {



        // Start the Interceptify Hook
        System.out.println("Starting Salacious Hook with args: " + Arrays.toString(args));

        // Find and Use Mods Enabled Config
        Path path = Paths.get(Paths.get("").toAbsolutePath() + "/PZHook_EnabledMods.cfg");
        if (!Files.exists(path))
            System.out.println("WARNING: No PZHook_EnabledMods.cfg config file found at: " + path);
        else {
            // Found Mods Enabled Config File so set Them to Enabled
            Object[] enabledmods = Files.lines(path).toArray();
            cfg.getMods().forEach((mod) -> {
                if (Arrays.asList(enabledmods).contains(mod.getModName()))
                    mod.setEnabled(true);
            });
        }

        System.out.println("Calling Interceptify EntryPoint");
        EntryPoint.entryPoint(cfg, args);
    }

    public static void premain(String args, Instrumentation instr) {
        EntryPoint.premain(args, instr);
    }
}
