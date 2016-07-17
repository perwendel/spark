package spark.callback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EventManager {

    private HashMap<Event.Type, List<Event.Holder>> events = new HashMap<>();

    public void addEventHandler(Event.Priority priority, Event.Type type, Callbacks.ICallback callback) {
        Event.Holder holder = new Event.Holder(priority, callback);
        if (!events.containsKey(type)) {
            events.put(type, new ArrayList<>());
        }
        events.get(type).add(holder);
    }

    public Event handle(Event.Type type, Object... arguments) {
        Event event = new Event(type);
        if (events.containsKey(type)) {
            List<Event.Holder> holders = events.get(type);
            Collections.sort(holders);
            for (Event.Holder holder : holders) {
                String iFace = holder.getCallback().getInterfaceType().getName();
                try {
                    Method handle = Class.forName(iFace).getMethods()[0];
                    handle.invoke(holder.getCallback(), new ArrayList<Object>() {{
                        add(event);
                        for (Object argument : arguments) {
                            add(argument);
                        }
                    }}.toArray());
                } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return event;
    }
}
