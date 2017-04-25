package spark.event;

import spark.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by kendrick on 4/24/17.
 */
public class EventManager {

    private static EventManager INSTANCE;

    public synchronized static EventManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventManager();
        }
        return INSTANCE;
    }

    private Map<EventType,List<EventListener>> listenerMap = new HashMap<EventType,List<EventListener>>();

    private EventManager() {}


    public void addEventListener(EventType type, EventListener listener) {
        List<EventListener> listenerList = listenerMap.get(type);
        if (listenerList == null) {
            listenerList = new LinkedList<EventListener>();
            listenerMap.put(type,listenerList);
        }
        listenerList.add(listener);
    }

    public void fireEvent(EventType type,Service service) {
        List<EventListener> listenerList = listenerMap.get(type);
        if (listenerList != null) {
            for (EventListener listener: listenerList) {
                listener.handleEvent(new Event(type,service));
            }
        }
    }
    public void fireEvent(EventType type) {
        List<EventListener> listenerList = listenerMap.get(type);
        if (listenerList != null) {
            for (EventListener listener: listenerList) {
                listener.handleEvent(new Event(type));
            }
        }
    }

}
