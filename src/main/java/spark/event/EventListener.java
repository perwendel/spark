package spark.event;

import spark.Service;

/**
 *
 * Interface to handle events
 *
 */
@FunctionalInterface
public interface EventListener {
    void handleEvent(Event e);
}
