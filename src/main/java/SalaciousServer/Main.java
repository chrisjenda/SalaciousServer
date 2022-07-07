package SalaciousServer;

import SalaciousServer.core.SalaciousServerBootstrap;
import SalaciousServer.core.SalaciousServerClassLoader;
import SalaciousServer.intercepts.interceptify.internal.ClassInjector;
import SalaciousServer.intercepts.interceptify.internal.ClassMetadata;
import SalaciousServer.logging.SalaciousServerLogger;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;

public class Main {

  private static ClassInjector ci;
  private static SalaciousServerClassLoader classLoader;

  public static Properties p = new Properties();

  public static void main(String[] args) {

    try {

      Class.forName("SalaciousServer.core.SalaciousServerClassTransformers", true, classLoader);
      Class.forName("SalaciousServer.logging.ZomboidLogger", true, classLoader);

      // redirect uncaught exception logs to Log4J
      Thread.setDefaultUncaughtExceptionHandler(
          new SalaciousServerLogger.Log4JUncaughtExceptionHandler());

      // initialize dispatcher system
      Class<?> eventHandler =
          classLoader.loadClass("SalaciousServer.event.SalaciousServerEventHandler");
      Class<?> eventDispatcher =
          classLoader.loadClass("SalaciousServer.event.SalaciousServerEventDispatcher");
      eventDispatcher
          .getDeclaredMethod("registerEventHandler", Class.class)
          .invoke(null, eventHandler);
    } catch (ClassNotFoundException
        | InvocationTargetException
        | IllegalAccessException
        | NoSuchMethodException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    Thread.setDefaultUncaughtExceptionHandler(
        new SalaciousServerLogger.Log4JUncaughtExceptionHandler());

    Class<?> entryPointClass = null;
    try {
      entryPointClass = classLoader.loadClass("zombie.network.GameServer");
      Method entryPoint = entryPointClass.getMethod("main", String[].class);
      SalaciousServerLogger.info("Launching Project Zomboid...");

      entryPoint.invoke(null, (Object) args);
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | InvocationTargetException
        | IllegalAccessException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static void premain(String args, Instrumentation instr) {

    SalaciousServerLogger.initialize();

    classLoader = SalaciousServerBootstrap.CLASS_LOADER;

    HashSet<String> test = new HashSet<>(2);
    test.add("zombie.gameStates.GameServer");
    var bb = new ByteBuddy();
    ci =
        new ClassInjector(
            args == null || args.isEmpty()
                ? bb
                : bb.with(ClassFileVersion.ofJavaVersionString(args)),
            instr);

    ci.defineMakePublicList(test);
    // ci.defineMakePublicPredicate("");
    // .collectMetadataFrom(jarFiles)

    List<ClassMetadata> classes = new ArrayList<ClassMetadata>();
    SalaciousServerLogger.info("Initializing Hooks");

    // Static Link Hooks here because I can't figure out how to scan package for classes

    try {
      classes.add(
          ci.setClassPath(
                  new URL[] {new URL("file:SalaciousServer/intercepts/GameServerHook.class")})
              .getClassMetadata("SalaciousServer.intercepts.GameServerHook"));

      classes.add(
          ci.setClassPath(
                  new URL[] {new URL("file:SalaciousServer/intercepts/DiscordBotHook.class")})
              .getClassMetadata("SalaciousServer.intercepts.DiscordBotHook"));

    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    ci.applyAnnotationsAndIntercept(classes, classLoader);
  }

  /**
   * Placeholder function for future runtime Java Agent registration.
   *
   * @param args args from the JVM
   * @param inst an {@link Instrumentation} instance from the JVM
   */
  @SuppressWarnings("unused")
  public static void agentmain(String args, Instrumentation inst) {
    premain(args, inst);
  }
}
