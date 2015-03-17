package spark.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Access;
import spark.route.RouteMatcherFactory;
import spark.webserver.InitParameters;
import spark.webserver.MatcherHandler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * HttpServlet that can be configured to be used in a web.xml file.
 * Needs the init parameter 'applicationClass' set to the application class where
 * the adding of routes should be made.
 *
 * @author Per Wendel
 * @author Andrei Varabyeu
 */
public class SparkServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SparkServlet.class);

    private String filterPath;
    private MatcherHandler matcherHandler;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        Access.runFromServlet();

        final SparkApplication application = getApplication(servletConfig);
        application.init();

        filterPath = FilterTools.getHandlerPath(InitParameters.ofServlet(servletConfig));
        matcherHandler = new MatcherHandler(RouteMatcherFactory.get(), false);
    }

    /**
     * @see {@link spark.servlet.SparkHandler#getApplication(spark.webserver.InitParameters)}
     */
    protected SparkApplication getApplication(ServletConfig servletConfig) throws ServletException {
        return SparkHandler.getApplication(InitParameters.ofServlet(servletConfig));
    }

    @Override
    public void service(ServletRequest request, ServletResponse response) throws
            IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request; // NOSONAR

        final String relativePath = FilterTools.getRelativePath(httpRequest, filterPath);

        if (LOG.isDebugEnabled()) {
            LOG.debug(relativePath);
        }

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getRequestURI() {
                return relativePath;
            }
        };

        if (SparkHandler.handleStaticResources(httpRequest, response)) {
            return;
        }
        matcherHandler.service(requestWrapper, response);
    }

    @Override
    public void destroy() {
        // ignore
    }
}
