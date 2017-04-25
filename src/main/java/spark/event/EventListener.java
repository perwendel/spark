package spark.event;

import spark.Service;

/**
 * Created by kendrick on 4/24/17.
 */
@FunctionalInterface
public interface EventListener {
    void handleEvent(Event e);
}
