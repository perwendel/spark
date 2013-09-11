package spark.webserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;

/**
 * Use to install a SessionIdManager when the server is initialized
 * 
 * @author Galen Dunkleberger
 *
 */
public interface SparkSessionIdManager {

    public SessionIdManager getSessionIdManager(Server server);

}
