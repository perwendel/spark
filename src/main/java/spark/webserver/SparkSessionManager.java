package spark.webserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;

public interface SparkSessionManager {

    public SessionManager getSessionManager(Server server);
}
