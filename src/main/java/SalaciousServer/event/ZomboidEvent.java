package SalaciousServer.event;

/**
 * This class represents game events recognized by SalaciousServer. These events are created by
 * installed {@code SalaciousServerHook}s and included as a method parameter in a callback to {@link
 * SalaciousServerEventDispatcher}. They are then dispatched to all methods that subscribe to those
 * specific events.
 */
@SuppressWarnings("WeakerAccess")
public interface ZomboidEvent {

  /** Returns a readable name that identifies this event. */
  String getName();
}
