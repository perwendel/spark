/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;

public class Spark {

    private static final String NAME = "Spark";

    public Spark() {
        this(4567);
    }

    public Spark(int port) {
        System.setProperty("org.mortbay.log.class", "spark.JettyLogger");

        Server server = new Server();
        SocketConnector connector = new SocketConnector();

        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(port);
        server.setConnectors(new Connector[] {connector});

        server.setHandler(new Context());

        try {
            System.out.println("== " + NAME + " has taken the stage ...");
            System.out.println(">> Listening on 0.0.0.0:" + port);

            server.start();
            System.in.read();

            System.out.println(">>> " + NAME + " shutting down...");

            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

}
