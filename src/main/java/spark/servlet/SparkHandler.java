package spark.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.resource.*;
import spark.utils.IOUtils;
import spark.webserver.InitParameters;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal handler. Contains common logic  for servlet and filter such as static resources handling
 *
 * @author Per Wendel
 * @author Andrei Varabyeu
 */
public class SparkHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SparkHandler.class);

    public static final String APPLICATION_CLASS_PARAM = "applicationClass";

    static List<AbstractResourceHandler> staticResourceHandlers = null;

    static boolean staticResourcesSet = false;
    static boolean externalStaticResourcesSet = false;

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public static void configureStaticResources(String folder) {
        if (!staticResourcesSet) {
            if (folder != null) {
                try {
                    ClassPathResource resource = new ClassPathResource(folder);
                    if (resource.getFile().isDirectory()) {
                        if (staticResourceHandlers == null) {
                            staticResourceHandlers = new ArrayList<>();
                        }
                        staticResourceHandlers.add(new ClassPathResourceHandler(folder, "index.html"));
                        LOG.info("StaticResourceHandler configured with folder = " + folder);
                    } else {
                        LOG.error("Static resource location must be a folder");
                    }
                } catch (IOException e) {
                    LOG.error("Error when creating StaticResourceHandler", e);
                }
            }
            staticResourcesSet = true;
        }
    }

    /**
     * Configures location for static resources
     *
     * @param folder the location
     */
    public static void configureExternalStaticResources(String folder) {
        if (!externalStaticResourcesSet) {
            if (folder != null) {
                try {
                    ExternalResource resource = new ExternalResource(folder);
                    if (resource.getFile().isDirectory()) {
                        if (staticResourceHandlers == null) {
                            staticResourceHandlers = new ArrayList<>();
                        }
                        staticResourceHandlers.add(new ExternalResourceHandler(folder, "index.html"));
                        LOG.info("External StaticResourceHandler configured with folder = " + folder);
                    } else {
                        LOG.error("External Static resource location must be a folder");
                    }
                } catch (IOException e) {
                    LOG.error("Error when creating external StaticResourceHandler", e);
                }
            }
            externalStaticResourcesSet = true;
        }
    }

    static boolean handleStaticResources(HttpServletRequest request, ServletResponse response) throws IOException {
        // handle static resources
        if (SparkHandler.staticResourceHandlers != null) {
            for (AbstractResourceHandler staticResourceHandler : SparkHandler.staticResourceHandlers) {
                AbstractFileResolvingResource resource = staticResourceHandler.getResource(request);
                if (resource != null && resource.isReadable()) {
                    IOUtils.copy(resource.getInputStream(), response.getOutputStream());
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Returns an instance of {@link SparkApplication} which on which {@link SparkApplication#init() init()} will be called.
     * Default implementation looks up the class name in the filterConfig using the key {@link #APPLICATION_CLASS_PARAM}.
     * Subclasses can override this method to use different techniques to obtain an instance (i.e. dependency injection).
     *
     * @param config the filter/servlet configuration for retrieving parameters passed to this filter.
     * @return the spark application containing the configuration.
     * @throws javax.servlet.ServletException if anything went wrong.
     */
    static SparkApplication getApplication(InitParameters config) throws ServletException {
        try {
            String applicationClassName = config.getInitParameter(APPLICATION_CLASS_PARAM);
            Class<?> applicationClass = Class.forName(applicationClassName);
            return (SparkApplication) applicationClass.newInstance();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
