package PZHook_server;

import PZHook_server.config.HookConfig;
import net.uptheinter.interceptify.EntryPoint;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

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

        // Generate Ascii Text from Server Name and Print it.
        BufferedImage bufferedImage = new BufferedImage(128, 24, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setFont(new Font("Padauk", Font.BOLD, 16));
        Iterator<String> it = Arrays.stream(args).iterator();
        while (it.hasNext()) {
            String arg = it.next();
            if (arg.contains("servername")) {
                graphics2D.drawString(args[arg.indexOf("servername")].toUpperCase(), 24, ((int) (24 * 0.67)));
            }
        }
        System.out.println("\n");
        for (int y = 0; y < 24; y++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int x = 0; x < 128; x++) {
                final String alphabet = "&#$@%*";
                final int N = alphabet.length();
                Random rd = new Random();
                stringBuilder.append(bufferedImage.getRGB(x, y) == -16777216 ? " " : alphabet.charAt(rd.nextInt(N)));
            }
            if (stringBuilder.toString().trim().isEmpty()) { continue; }
            System.out.println(stringBuilder);
        }
        System.out.println("\n");

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
        EntryPoint.entryPoint(cfg, args);
    }

    public static void premain(String args, Instrumentation instr) {
        EntryPoint.premain(args, instr);
    }
}
