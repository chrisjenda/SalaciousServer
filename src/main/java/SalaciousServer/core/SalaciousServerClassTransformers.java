package SalaciousServer.core;

import SalaciousServer.hook.SalaciousServerHook;
import SalaciousServer.patch.DebugLogPatch;
import SalaciousServer.patch.DebugLogStreamPatch;
import SalaciousServer.patch.ZomboidPatch;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * This class defines, initializes and stores {@link SalaciousServerClassTransformer} instances. To
 * retrieve a mapped instance of registered transformer call {@link #getRegistered(String)}.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SalaciousServerClassTransformers {

  /**
   * Internal registry of created transformers. This map is checked for entries by {@link
   * SalaciousServerClassLoader} when loading classes and invokes the transformation chain of
   * methods to transform the class before defining it via JVM.
   */
  private static final Map<String, SalaciousServerClassTransformer> TRANSFORMERS = new HashMap<>();

  static {
    /////////////////////
    // REGISTER HOOKS //
    ///////////////////

    ///////////////////////
    // REGISTER PATCHES //
    /////////////////////

    registerTransformer("zombie.debug.DebugLog", new DebugLogPatch());
    // registerTransformer("zombie.gameStates.GameLoadingState", new GameLoadingStatePatch());
    registerTransformer(
        "zombie.debug.DebugLogStream",
        new DebugLogStreamPatch(),
        ImmutableMap.<MethodData, MethodMaxs>builder()
            .put(
                new MethodData(
                    "printException",
                    "(Ljava/lang/Throwable;Ljava/lang/String;"
                        + "Ljava/lang/String;Lzombie/debug/LogSeverity;)V"),
                new MethodMaxs(5, 6))
            .build());
  }

  /**
   * Register designated {@link SalaciousServerClassTransformer} with given name.
   *
   * @param className name of the target class to transform.
   * @param transformer {@code SalaciousServerClassTransformer} to register.
   */
  private static void registerTransformer(
      String className, SalaciousServerClassTransformer transformer) {
    TRANSFORMERS.put(className, transformer);
  }

  /**
   * Create and register a new {@link SalaciousServerClassTransformer} with given name that applies
   * a {@link ZomboidPatch} designated by method parameter.
   *
   * @param className name of the target class to transform.
   * @param patch {@code ZomboidPatch} to apply with transformation.
   */
  private static void registerTransformer(String className, ZomboidPatch patch) {

    TRANSFORMERS.put(
        className,
        new SalaciousServerClassTransformer(className) {

          @Override
          SalaciousServerClassTransformer transform() {

            patch.applyPatch(this);
            return this;
          }
        });
  }

  /**
   * Create and register a new {@link SalaciousServerClassTransformer} with given name that applies
   * a {@link ZomboidPatch} designated by method parameter. Additionally this method also defines
   * the maximum stack size of methods in visited class.
   *
   * @param className name of the target class to transform.
   * @param patch {@code ZomboidPatch} to apply with transformation.
   * @param maxStacks maximum stack size mapped to method data.
   */
  private static void registerTransformer(
      String className, ZomboidPatch patch, Map<MethodData, MethodMaxs> maxStacks) {

    ClassNode visitor =
        new ClassNode(Opcodes.ASM9) {

          @Override
          public MethodVisitor visitMethod(
              int access, String name, String descriptor, String signature, String[] exceptions) {

            for (Map.Entry<MethodData, MethodMaxs> entry : maxStacks.entrySet()) {
              MethodData data = entry.getKey();
              if (name.equals(data.name) && descriptor.equals(data.descriptor)) {
                MethodMaxs maxData = entry.getValue();
                MethodNode method =
                    new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions) {
                      @Override
                      public void visitMaxs(int maxStack, int maxLocals) {
                        super.visitMaxs(
                            maxData.maxStack > 0 ? maxData.maxStack : maxStack,
                            maxData.maxLocal > 0 ? maxData.maxLocal : maxLocals);
                      }
                    };
                methods.add(method);
                return method;
              }
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
          }
        };
    registerTransformer(className, patch);
  }

  /**
   * Create and register a new {@link SalaciousServerClassTransformer} with given name that installs
   * a {@link SalaciousServerHook} designated by method parameter. Additionally this method also
   * defines the maximum stack size of methods in visited class.
   *
   * @param className name of the target class to transform.
   * @param hook {@link SalaciousServerHook} to install with transformation.
   * @param maxStacks maximum stack size mapped to method data.
   */
  private static void registerTransformer(
      String className, SalaciousServerHook hook, Map<MethodData, MethodMaxs> maxStacks) {

    ClassNode visitor =
        new ClassNode(Opcodes.ASM9) {

          @Override
          public MethodVisitor visitMethod(
              int access, String name, String descriptor, String signature, String[] exceptions) {

            for (Map.Entry<MethodData, MethodMaxs> entry : maxStacks.entrySet()) {
              MethodData data = entry.getKey();
              if (name.equals(data.name) && descriptor.equals(data.descriptor)) {
                MethodMaxs maxData = entry.getValue();
                MethodNode method =
                    new MethodNode(Opcodes.ASM9, access, name, descriptor, signature, exceptions) {
                      @Override
                      public void visitMaxs(int maxStack, int maxLocals) {
                        super.visitMaxs(
                            maxData.maxStack > 0 ? maxData.maxStack : maxStack,
                            maxData.maxLocal > 0 ? maxData.maxLocal : maxLocals);
                      }
                    };
                methods.add(method);
                return method;
              }
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
          }
        };
    TRANSFORMERS.put(
        className,
        new SalaciousServerClassTransformer(className, visitor) {

          @Override
          SalaciousServerClassTransformer transform() {

            hook.installHook(this);
            return this;
          }
        });
  }

  /**
   * Create and register a new {@link SalaciousServerClassTransformer} with given name that installs
   * a {@link SalaciousServerHook} designated by method parameter.
   *
   * @param className name of the target class to transform.
   * @param hook {@link SalaciousServerHook} to install with transformation.
   */
  private static void registerTransformer(String className, SalaciousServerHook hook) {

    TRANSFORMERS.put(
        className,
        new SalaciousServerClassTransformer(className) {

          @Override
          SalaciousServerClassTransformer transform() {

            hook.installHook(this);
            return this;
          }
        });
  }

  /**
   * Returns registered instance of {@link SalaciousServerClassTransformer} that matches the given
   * name.
   *
   * @return {@code SalaciousServerClassTransformer} or {@code null} if no registered instance
   *     found.
   */
  @Contract(pure = true)
  public static @Nullable SalaciousServerClassTransformer getRegistered(String className) {
    return TRANSFORMERS.getOrDefault(className, null);
  }

  private static class MethodData {

    private final String name;
    private final String descriptor;

    private MethodData(String name, String descriptor) {

      this.name = name;
      this.descriptor = descriptor;
    }
  }

  private static class MethodMaxs {

    /**
     * Maximum stack size of the method. Value of {@code 0} indicates that original value should be
     * used.
     */
    private final int maxStack;

    /**
     * Maximum number of local variables for the method. Value of {@code 0} indicates that original
     * value should be used.
     */
    private final int maxLocal;

    private MethodMaxs(int maxStack, int maxLocal) {

      this.maxStack = maxStack;
      this.maxLocal = maxLocal;
    }

    private MethodMaxs(int maxStack) {

      this.maxStack = maxStack;
      this.maxLocal = 0;
    }
  }
}
