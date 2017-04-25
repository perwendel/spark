package spark.event;

import spark.Service;

import java.util.*;

/**
 * Singleton that maintain a list of event listeners by type and notify the event listeners when event is fired.
 *
 */
public class EventManager {

    // singleton instance
    private static EventManager INSTANCE;

    public synchronized static EventManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventManager();
        }
        return INSTANCE;
    }

    private Map<EventType,List<EventListener>> listenerMap = new HashMap<EventType,List<EventListener>>();

    private EventManager() {}

    /**
     *
     * Register the event listener with the manager
     *
     * @param type event types
     * @param listener callback for event listener
     */
    public synchronized void addEventListener(EventType type, EventListener listener) {
        List<EventListener> listenerList = listenerMap.get(type);
        if (listenerList == null) {
            listenerList = new LinkedList<EventListener>();
            listenerMap.put(type,listenerList);
        }
        listenerList.add(listener);
    }

    /**
     *
     * Used to notify event listeners when events happen
     *
     * @param type event types
     * @param service service that raise the event
     */
    public void fireEvent(EventType type,Service service) {
        List<EventListener> listenerList = listenerMap.get(type);
        if (listenerList != null) {
            for (EventListener listener: listenerList) {
                listener.handleEvent(new Event(type,service));
            }
        }
    }

    /**
     *
     * Used to notify event listeners when events happen
     *
     * @param type
     */
    public void fireEvent(EventType type) {
        List<EventListener> listenerList = listenerMap.get(type);
        if (listenerList != null) {
            for (EventListener listener: listenerList) {
                listener.handleEvent(new Event(type));
            }
        }
    }

}
