package Engine.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A simple, scene-local event bus for decoupled communication between game objects.
 * It allows objects to subscribe to and publish specific event types.
 */
public class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> subscribers = new HashMap<>();

    /**
     * Subscribes a listener to a specific type of event.
     *
     * @param eventType The class of the event to listen for.
     * @param listener  The callback to execute when the event is published.
     * @param <T>       The type of the event.
     */
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add((Consumer<Object>) listener);
    }

    /**
     * Unsubscribes a listener from a specific type of event.
     *
     * @param eventType The class of the event.
     * @param listener  The callback to remove.
     * @param <T>       The type of the event.
     */
    public <T> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<Object>> listeners = subscribers.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Publishes an event to all registered listeners.
     *
     * @param event The event object to publish.
     */
    public void publish(Object event) {
        List<Consumer<Object>> listeners = subscribers.get(event.getClass());
        if (listeners != null) {
            // Iterate over a copy to avoid ConcurrentModificationException if a listener unsubscribes.
            for (Consumer<Object> listener : new ArrayList<>(listeners)) {
                listener.accept(event);
            }
        }
    }
}
