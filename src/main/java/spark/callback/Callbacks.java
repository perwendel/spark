package spark.callback;

import spark.FilterImpl;
import spark.RouteImpl;
import spark.embeddedserver.EmbeddedServer;

public class Callbacks {

    public interface ICallback {
        default Class getInterfaceType() {
            return this.getClass().getInterfaces()[0];
        }
    }

    @FunctionalInterface
    public interface IServerCallback extends ICallback {
        void handle(Event event, EmbeddedServer server);
    }

    @FunctionalInterface
    public interface IRouteCallback extends ICallback {
        void handle(Event event, RouteImpl route);
    }

    @FunctionalInterface
    public interface IFilterCallback extends ICallback {
        void handle(Event event, FilterImpl route);
    }
}
