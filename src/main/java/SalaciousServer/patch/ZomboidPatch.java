package SalaciousServer.patch;

import SalaciousServer.core.SalaciousServerClassTransformer;

/**
 * This class represents a Project Zomboid {@code Class} code patch. A code patch is a series of
 * instructions that are introduced with ASM class transformation to remove and/or changes code
 * lines. Patch can also add new lines of code but unlike hooks it also removes or changes existing
 * code lines which makes it a much more intrusive.
 *
 * <p>Patches should be applied only in situations where new functionality (which cannot be
 * implemented with hooks) is being introduced to the game engine.
 */
public interface ZomboidPatch {

  /** Apply a code patch with the given {@link SalaciousServerClassTransformer}. */
  void applyPatch(SalaciousServerClassTransformer transformer);
}
