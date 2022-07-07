package SalaciousServer.hook;

import SalaciousServer.core.SalaciousServerClassTransformer;
import SalaciousServer.event.SalaciousServerEventDispatcher;
import SalaciousServer.event.ZomboidEvent;

/**
 * This interface represents a hook in game code that creates an instance of {@link ZomboidEvent}
 * and calls {@link SalaciousServerEventDispatcher}. The dispatcher then forwards the event to all
 * registered methods that have subscribed to event in context. Hooks are only intended for
 * <b>internal</b> use to generate and send events from game code.
 *
 * @see SalaciousServerEventDispatcher#dispatchEvent(ZomboidEvent)
 */
public interface SalaciousServerHook {

  /**
   * Use the given transformer to install {@link SalaciousServerHook} in game code. The installation
   * is done by altering method bytecode with the use of ASM to create an instance of {@link
   * ZomboidEvent} and create a direct callback to {@link SalaciousServerEventDispatcher} class.
   * Note that the transformers used to install hooks need to be registered in the static block of
   * {@link SalaciousServerClassTransformer}.
   *
   * @param transformer {@link SalaciousServerClassTransformer} to use to install the hook.
   */
  void installHook(SalaciousServerClassTransformer transformer);
}
