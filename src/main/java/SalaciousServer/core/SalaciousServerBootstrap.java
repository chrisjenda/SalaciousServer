package SalaciousServer.core;

import java.lang.reflect.Method;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("WeakerAccess")
public class SalaciousServerBootstrap {

  /**
   * {@code ClassLoader} used to transform and load all needed classes. This includes both Project
   * Zomboid and mod classes. Because class loaders maintain their own set of class instances and
   * native libraries this loader should always be used to load classes that access or modify
   * transformed class fields or methods.
   */
  public static final SalaciousServerClassLoader CLASS_LOADER = new SalaciousServerClassLoader();

  /**
   * Loaded and initialized {@link SalaciousServerClassTransformer} {@code Class}. To transform
   * specific classes during load time (<i>on-fly</i>) {@link SalaciousServerClassLoader} has to
   * read and invoke registered transformers. Due to how class loading works in Java references to
   * classes within {@code ClassLoader} do not get loaded by that specific {@code ClassLoader} but
   * get delegate to {@code AppClassLoader}. For this reason we have to use bootstrapping and
   * reflection to access transformers from {@code SalaciousServerClassLoader}.
   */
  private static final Class<?> TRANSFORMER_CLASS;

  /** Loaded and initialized {@link SalaciousServerClassTransformers} {@code Class}. */
  private static final Class<?> TRANSFORMERS_CLASS;

  /**
   * Represents {@link SalaciousServerClassTransformers#getRegistered(String)} method.
   *
   * @see #getRegisteredTransformer(String)
   */
  private static final Method TRANSFORMER_GETTER;

  /**
   * Represents {@link SalaciousServerClassTransformer#transform(byte[])} method.
   *
   * @see #invokeTransformer(Object, byte[])
   */
  private static final Method TRANSFORMER_INVOKER;

  /**
   * Marks the {@code SalaciousServerBoostrap} as being fully loaded. This variable will be {@code
   * true} when the static block has finished initializing. Required by classes that are loaded
   * before {@code SalaciousServerBoostrap} but still depend on it.
   */
  private static final boolean hasLoaded;

  static {
    try {
      TRANSFORMER_CLASS =
          Class.forName("SalaciousServer.core.SalaciousServerClassTransformer", true, CLASS_LOADER);
      TRANSFORMERS_CLASS =
          Class.forName(
              "SalaciousServer.core.SalaciousServerClassTransformers", true, CLASS_LOADER);
      TRANSFORMER_GETTER = TRANSFORMERS_CLASS.getDeclaredMethod("getRegistered", String.class);
      TRANSFORMER_INVOKER = TRANSFORMER_CLASS.getDeclaredMethod("transform", byte[].class);

      // mark SalaciousServerBootstrap as finished loading
      hasLoaded = true;
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns registered instance of {@link SalaciousServerClassTransformer} that matches the given
   * name.
   *
   * @throws ReflectiveOperationException if an error occurred while invoking method.
   * @see SalaciousServerClassTransformers#getRegistered(String)
   */
  static @Nullable Object getRegisteredTransformer(String name)
      throws ReflectiveOperationException {
    return TRANSFORMER_GETTER.invoke(null, name);
  }

  /**
   * Calls method chain to transform the given {@code Class} byte array.
   *
   * @throws ReflectiveOperationException if an error occurred while invoking method.
   * @see SalaciousServerClassTransformer#transform(byte[])
   */
  @SuppressWarnings("RedundantCast")
  static byte[] invokeTransformer(Object transformer, byte[] rawClass)
      throws ReflectiveOperationException {
    return (byte[]) TRANSFORMER_INVOKER.invoke(transformer, (Object) rawClass);
  }

  /**
   * Returns if {@code SalaciousServerBoostrap} has finished loading.
   *
   * @return {@code true} if boostrap has been fully loaded.
   */
  static boolean hasLoaded() {
    return hasLoaded;
  }
}
