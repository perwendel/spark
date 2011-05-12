package spark;

import spark.route.RouteMatcherFactory;

public class SparkRunner {

    /**
     * Initializes and ignites spark
     */
    public static void run() {
        MatcherFilter matcherFilter = new MatcherFilter(RouteMatcherFactory.create());
        matcherFilter.init(null);
        JettyHandler handler = new JettyHandler(matcherFilter);
        new Spark(handler);
    }
    
}
