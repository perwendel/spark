package spark.embeddedserver.jetty.eventsource;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.EventSourceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EventSourceServletContextHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(EventSourceServletContextHandlerFactory.class);

    /**
     * Creates a new eventSource servlet context handler.
     *
     * @param eventSourceHandlers          eventSourceHandlers
     * @return a new eventSource servlet context handler or 'null' if creation failed.
     */
    public static ServletContextHandler create(Map<String, EventSourceHandlerWrapper> eventSourceHandlers) {
        ServletContextHandler eventSourceServletContextHandler = null;
        if (eventSourceHandlers != null) {
            try {
                eventSourceServletContextHandler = new ServletContextHandler(null, "/", true, false);
                addToExistingContext(eventSourceServletContextHandler, eventSourceHandlers);
            } catch (Exception ex) {
                logger.error("creation of event source context handler failed.", ex);
                eventSourceServletContextHandler = null;
            }
        }
        return eventSourceServletContextHandler;
    }

    public static void addToExistingContext(ServletContextHandler contextHandler, Map<String, EventSourceHandlerWrapper> eventSourceHandlers){
        if (eventSourceHandlers == null)
            return;
        eventSourceHandlers.forEach((path, servletWrapper)->
            contextHandler.addServlet(new ServletHolder((EventSourceServlet)servletWrapper.getHandler()), path));
    }
}
