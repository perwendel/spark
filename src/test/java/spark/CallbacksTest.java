package spark;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.callback.Callbacks;
import spark.callback.Event;

import static spark.Spark.event;
import static spark.Spark.get;

public class CallbacksTest {

    private static boolean fired = false;
    private static List<Event.Priority> ordered = new ArrayList<>();
    private static List<Event.Type> types = new ArrayList<>();

    @BeforeClass
    public static void before() {
        event(Event.Type.SERVER_STOPPED, (Callbacks.IServerCallback) (event, server) -> fired = true);

        event(Event.Priority.LOW, Event.Type.SERVER_STOPPED, (Callbacks.IServerCallback) (event, server) -> ordered.add(Event.Priority.LOW));
        event(Event.Priority.HIGH, Event.Type.SERVER_STOPPED, (Callbacks.IServerCallback) (event, server) -> ordered.add(Event.Priority.HIGH));
        event(Event.Priority.NORMAL, Event.Type.SERVER_STOPPED, (Callbacks.IServerCallback) (event, server) -> ordered.add(Event.Priority.NORMAL));

        event(Event.Type.ROUTE_ADDED, (Callbacks.IRouteCallback) (event, route) -> {
            System.out.println(event.getType() + " " + route.getPath() + " " + route.getAcceptType());
        });

        get("/test", (request, response) -> "Test");
    }

    @AfterClass
    public static void after() {
        Spark.stop();
    }

    @Test
    public void firedTest() {
        Spark.stop();
        Assert.assertTrue(fired);
        Spark.init();
    }

    @Test
    public void orderedTest() {
        ordered = new ArrayList<>();
        Spark.stop();
        Assert.assertArrayEquals(ordered.toArray(), new Event.Priority[] {Event.Priority.HIGH, Event.Priority.NORMAL, Event.Priority.LOW});
        Spark.init();
    }

}
