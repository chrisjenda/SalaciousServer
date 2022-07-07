package SalaciousServer.util;

import org.jetbrains.annotations.Contract;

public class SalaciousServerUtils {

  /**
   * Returns a path representation of the name for given {@code Class}.
   *
   * @param clazz {@code Class} whose name should be converted to path.
   * @return {@code String} representing a path.
   */
  @Contract(pure = true)
  public static String getClassAsPath(Class<?> clazz) {
    return clazz.getName().replace('.', '/');
  }
}
