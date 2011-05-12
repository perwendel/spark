package spark;

import spark.webserver.SparkServer;
import spark.webserver.SparkServerFactory;

public class SparkIgniter {
    
    public static void main(String[] args) {
        ignite();
    }
    
    /**
     * Initializes and ignites spark
     */
    public static void ignite() {
        SparkServer server = SparkServerFactory.create();
        server.ignite();
    }
    
}
