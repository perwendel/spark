package spark;

import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

public class SparkRunner {
    
    public static void main(String[] args) {
        run();
    }
    
    /**
     * Initializes and ignites spark
     */
    public static void run() {
        SparkServer server = SparkServerFactory.create();
        server.ignite();
    }
    
}
