package spark.webserver;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;

/**
 * Just a simple abstraction around servlet and filter configuration.
 *
 * @author Andrei Varabyeu
 */
public abstract class InitParameters {

    abstract public String getInitParameter(String name);

    public static InitParameters ofServlet(ServletConfig servletConfig) {
        return new InitParameters() {
            @Override
            public String getInitParameter(String name) {
                return servletConfig.getInitParameter(name);
            }
        };
    }

    public static InitParameters ofFilter(FilterConfig filterConfig) {
        return new InitParameters() {
            @Override
            public String getInitParameter(String name) {
                return filterConfig.getInitParameter(name);
            }
        };
    }


}
