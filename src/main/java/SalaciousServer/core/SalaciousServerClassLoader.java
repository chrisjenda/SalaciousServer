package SalaciousServer.core;

import SalaciousServer.logging.SalaciousServerLogger;
import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("WeakerAccess")
public class SalaciousServerClassLoader extends ClassLoader {

  /**
   * {@code Set} of class prefixes that mark classes to not load with this {@code ClassLoader}. If a
   * class matches this patter it's loading will be delegated to the parent loader. Classes matching
   * this prefix are causing exceptions when loaded with this {@code ClassLoader}, and they also do
   * not need to be loaded with this {@code ClassLoader}.
   */
  @SuppressWarnings("SpellCheckingInspection")
  private static final ImmutableSet<String> CLASS_BLACKLIST =
      ImmutableSet.of(
          "java.",
          "org.objectweb.asm.",
          "sun.",
          "com.sun.",
          "org.xml.",
          "org.w3c.",
          "javax.script.",
          "javax.management.",
          "javax.imageio.",
          "javax.xml.",
          "javax.sql.",
          "jdk.internal.reflect.",
          "org.eclipse.jetty",
          "io.javalin",
          "javax.servlet.",
          "de.btobastian.",
          "org.javacord.",
          "SalaciousServer.logging.SalaciousServerLogger",
          "SalaciousServer.core.SalaciousServerBootstrap",
          "SalaciousServer.core.SalaciousServerClassLoader");
  /**
   * {@code ClassLoader} that is the parent of this {@code ClassLoader}. When loading classes, those
   * classes not matching the whitelist pattern will have their loading process delegated to this
   * {@code ClassLoader}.
   */
  private final ClassLoader parentClassLoader;
  /** {@code URLClassLoader} responsible for loading mod classes and assets. */

  /**
   * Create {@code SalaciousServerClassLoader} with additional locations to search for resources.
   *
   * @param resourceLocations the URLs from which to load classes and resources.
   */
  SalaciousServerClassLoader(URL[] resourceLocations) {

    parentClassLoader = getClass().getClassLoader();
  }

  public SalaciousServerClassLoader() {

    SalaciousServerLogger.info("Initialized SalaciousServerClassLoader");
    parentClassLoader = getClass().getClassLoader();
  }

  /**
   * Returns {@code true} if at least one prefix pattern in blacklist matches the given name. When a
   * class name is considered <i>blacklisted</i> it will not be loaded by this {@code ClassLoader}.
   */
  @Contract(pure = true)
  static boolean isBlacklistedClass(String name) {
    return CLASS_BLACKLIST.stream().anyMatch(name::startsWith);
  }

  /**
   * Defines and returns a package by name in this {@code ClassLoader}.
   *
   * @param name name of the package to define.
   * @return instance of {@link Package} defined or {@code null} if no package defined.
   * @throws IllegalArgumentException if package name duplicates an existing package either in this
   *     class loader or one of its ancestors
   */
  @Nullable
  Package definePackageForName(String name) {

    int pkgDelimiterPos = name.lastIndexOf('.');
    if (pkgDelimiterPos > 0) {
      String pkgString = name.substring(0, pkgDelimiterPos);
      if (getDefinedPackage(pkgString) == null) {
        return definePackage(pkgString, null, null, null, null, null, null, null);
      }
    }
    return null;
  }

  /**
   * Returns {@code true} if this loader has been recorded by the Java virtual machine as an
   * initiating loader of a class with the given binary name. Otherwise {@code null} is returned.
   *
   * @return {@code true} if class with given name has been loaded, {@code false} otherwise.
   */
  boolean isClassLoaded(String name) {
    return findLoadedClass(name) != null;
  }

  @Override
  public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

    // make sure our loading is not interrupted by other operations
    synchronized (getClassLoadingLock(name)) {
      return loadClassInternal(name, resolve);
    }
  }

  /**
   * Loads the class with the specified binary name. This method will get called every time a class
   * is about to be loaded by this {@code ClassLoader}. It is called before the class is defined or
   * loaded into JVM, which allows us to manipulate the class to our desire.
   *
   * @param name the binary name of the class.
   * @param resolve if {@code true} then resolve the class.
   * @return the resulting {@code Class} object.
   * @throws ClassNotFoundException if the class could not be found.
   */
  private Class<?> loadClassInternal(String name, boolean resolve) throws ClassNotFoundException {

    Class<?> clazz = findLoadedClass(name);
    if (clazz == null) {
      // SalaciousServerLogger.info("Preparing to load class " + name);
      if (!isBlacklistedClass(name)) {
        // SalaciousServerLogger.info("Loading with SalaciousServerClassLoader");
        try {
          byte[] input = getRawClassByteArray(name);

          if (SalaciousServerBootstrap.hasLoaded()) {
            Object transformer = SalaciousServerBootstrap.getRegisteredTransformer(name);
            if (transformer != null) {
              input = SalaciousServerBootstrap.invokeTransformer(transformer, input);
            }
          }
          if (input.length > 0) {
            // package has to be created before we define the class
            definePackageForName(name);

            clazz = defineClass(name, input, 0, input.length);
            if (clazz.getClassLoader() == this) {
              // SalaciousServerLogger.info("Successfully loaded class with
              // SalaciousServerClassLoader");
            } else
              throw new RuntimeException("Unable to load class with SalaciousServerClassLoader");
          }
        } catch (IOException | ReflectiveOperationException e) {
          throw new RuntimeException(e);
        }
      }
    }
    // if the class is not whitelisted delegate loading to parent class loader
    if (clazz == null) {
      // SalaciousServerLogger.info("Loading with application class loader");
      clazz = parentClassLoader.loadClass(name);
    }
    if (resolve) {
      resolveClass(clazz);
    }
    return clazz;
  }

  /** Converts the given class name to a file name. */
  @Contract(pure = true)
  private String getClassFileName(String name) {
    return name.replace('.', '/') + ".class";
  }

  /**
   * Reads the {@code Class} with given name into a {@code byte} array and return the result.
   *
   * @param name name of the {@code Class} to read.
   * @return {@code byte} array read from {@code Class} or an empty array if the {@code Class} with
   *     the given name could not be found.
   * @throws IOException if an I/O error occurred while reading or writing to stream.
   */
  byte[] getRawClassByteArray(String name) throws IOException {

    // opens an input stream to read the class for given name
    InputStream inputStream = getResourceAsStream(getClassFileName(name));
    if (inputStream == null) {
      return new byte[0];
    }
    int a = inputStream.available();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(a < 32 ? 32768 : a);
    /*
     * read from input stream and write to output stream
     * a maximum of 8192 bytes per write operation
     */
    int len;
    byte[] buffer = new byte[8192];
    while ((len = inputStream.read(buffer)) > 0) {
      outputStream.write(buffer, 0, len);
    }
    inputStream.close();
    return outputStream.toByteArray();
  }
}
