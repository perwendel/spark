package spark.event;

import spark.Service;

/**
 * Created by kendrick on 4/24/17.
 */
public class Event {
    private EventType eventType;
    private Service service;

    public Event(EventType eventType) {
        this.eventType = eventType;
    }

    public Event(EventType eventType, Service server) {
        this.eventType = eventType;
        this.service = service;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
