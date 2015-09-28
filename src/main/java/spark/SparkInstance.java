/*
 * Copyright 2015 - Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.globalstate.ServletFlag;
import spark.route.RouteMatcherFactory;
import spark.route.SimpleRouteMatcher;
import spark.ssl.SslStores;
import spark.staticfiles.StaticFiles;
import spark.webserver.SparkServerFactory;

import static java.util.Objects.requireNonNull;

/**
 * Holds the implementation of the Spark API. (previously in Spark and SparkBase).
 */
final class SparkInstance extends Routable {
    private static final Logger LOG = LoggerFactory.getLogger("spark.Spark");

    public static final int SPARK_DEFAULT_PORT = 4567;
    protected static final String DEFAULT_ACCEPT_TYPE = "*/*";

    protected boolean initialized = false;

    protected int port = SPARK_DEFAULT_PORT;
    protected String ipAddress = "0.0.0.0";

    protected SslStores sslStores;

    protected String staticFileFolder = null;
    protected String externalStaticFileFolder = null;

    protected Map<String, Class<?>> webSocketHandlers = null;

    protected int maxThreads = -1;
    protected int minThreads = -1;
    protected int threadIdleTimeoutMillis = -1;
    protected Optional<Integer> webSocketIdleTimeoutMillis = Optional.empty();

    protected SparkServer server;
    protected SimpleRouteMatcher routeMatcher;

    private boolean servletStaticLocationSet;
    private boolean servletExternalStaticLocationSet;

    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     * @deprecated replaced by {@link #ipAddress(String)}
     */
    public synchronized void setIpAddress(String ipAddress) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        this.ipAddress = ipAddress;
    }

    /**
     * Set the IP address that Spark should listen on. If not called the default
     * address is '0.0.0.0'. This has to be called before any route mapping is
     * done.
     *
     * @param ipAddress The ipAddress
     */
    public synchronized void ipAddress(String ipAddress) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        this.ipAddress = ipAddress;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     * @deprecated replaced by {@link #port(int)}
     */
    public synchronized void setPort(int port) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        this.port = port;
    }

    /**
     * Set the port that Spark should listen on. If not called the default port
     * is 4567. This has to be called before any route mapping is done.
     * If provided port = 0 then the an arbitrary available port will be used.
     *
     * @param port The port number
     */
    public synchronized void port(int port) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        this.port = port;
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     * @deprecated replaced by {@link #secure(String, String, String, String)}
     */
    public synchronized void setSecure(String keystoreFile,
                                       String keystorePassword,
                                       String truststoreFile,
                                       String truststorePassword) {

        secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    /**
     * Set the connection to be secure, using the specified keystore and
     * truststore. This has to be called before any route mapping is done. You
     * have to supply a keystore file, truststore file is optional (keystore
     * will be reused).
     * This method is only relevant when using embedded Jetty servers. It should
     * not be used if you are using Servlets, where you will need to secure the
     * connection in the servlet container
     *
     * @param keystoreFile       The keystore file location as string
     * @param keystorePassword   the password for the keystore
     * @param truststoreFile     the truststore file location as string, leave null to reuse
     *                           keystore
     * @param truststorePassword the trust store password
     */
    public synchronized void secure(String keystoreFile,
                                    String keystorePassword,
                                    String truststoreFile,
                                    String truststorePassword) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }

        if (keystoreFile == null) {
            throw new IllegalArgumentException(
                    "Must provide a keystore file to run secured");
        }

        sslStores = SslStores.create(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
    }

    /**
     * Configures the embedded web server's thread pool.
     *
     * @param maxThreads max nbr of threads.
     */
    public synchronized void threadPool(int maxThreads) {
        threadPool(maxThreads, -1, -1);
    }

    /**
     * Configures the embedded web server's thread pool.
     *
     * @param maxThreads        max nbr of threads.
     * @param minThreads        min nbr of threads.
     * @param idleTimeoutMillis thread idle timeout (ms).
     */
    public synchronized void threadPool(int maxThreads, int minThreads, int idleTimeoutMillis) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }

        this.maxThreads = maxThreads;
        this.minThreads = minThreads;
        this.threadIdleTimeoutMillis = idleTimeoutMillis;
    }

    /**
     * Sets the folder in classpath serving static files. Observe: this method
     * must be called before all other methods.
     *
     * @param folder the folder in classpath.
     */
    public synchronized void staticFileLocation(String folder) {
        if (initialized && !ServletFlag.isRunningFromServlet()) {
            throwBeforeRouteMappingException();
        }
        staticFileFolder = folder;
        if (!servletStaticLocationSet) {
            StaticFiles.configureStaticResources(staticFileFolder);
            servletStaticLocationSet = true;
        } else {
            LOG.warn("Static file location has already been set");
        }
    }

    /**
     * Sets the external folder serving static files. <b>Observe: this method
     * must be called before all other methods.</b>
     *
     * @param externalFolder the external folder serving static files.
     */
    public synchronized void externalStaticFileLocation(String externalFolder) {
        if (initialized && !ServletFlag.isRunningFromServlet()) {
            throwBeforeRouteMappingException();
        }
        externalStaticFileFolder = externalFolder;
        if (!servletExternalStaticLocationSet) {
            StaticFiles.configureExternalStaticResources(externalStaticFileFolder);
            servletExternalStaticLocationSet = true;
        } else {
            LOG.warn("External static file location has already been set");
        }
    }

    /**
     * Maps the given path to the given WebSocket handler.
     * <p>
     * This is currently only available in the embedded server mode.
     *
     * @param path    the WebSocket path.
     * @param handler the handler class that will manage the WebSocket connection to the given path.
     */
    public synchronized void webSocket(String path, Class<?> handler) {
        requireNonNull(path, "WebSocket path cannot be null");
        requireNonNull(handler, "WebSocket handler class cannot be null");
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        if (ServletFlag.isRunningFromServlet()) {
            throw new IllegalStateException("WebSockets are only supported in the embedded server");
        }
        if (webSocketHandlers == null) {
            webSocketHandlers = new HashMap<>();
        }
        webSocketHandlers.put(path, handler);
    }

    /**
     * Sets the max idle timeout in milliseconds for WebSocket connections.
     *
     * @param timeoutMillis The max idle timeout in milliseconds.
     */
    public synchronized void webSocketIdleTimeoutMillis(int timeoutMillis) {
        if (initialized) {
            throwBeforeRouteMappingException();
        }
        if (ServletFlag.isRunningFromServlet()) {
            throw new IllegalStateException("WebSockets are only supported in the embedded server");
        }
        webSocketIdleTimeoutMillis = Optional.of(timeoutMillis);
    }

    /**
     * Waits for the spark server to be initialized.
     * If it's already initialized will return immediately
     */
    public void awaitInitialization() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.info("Interrupted by another thread");
        }
    }

    private void throwBeforeRouteMappingException() {
        throw new IllegalStateException(
                "This must be done before route mapping has begun");
    }

    private boolean hasMultipleHandlers() {
        return webSocketHandlers != null;
    }


    /**
     * Stops the Spark server and clears all routes
     */
    public synchronized void stop() {
        if (server != null) {
            routeMatcher.clearRoutes();
            server.stop();
            latch = new CountDownLatch(1);
        }
        StaticFiles.clear();
        initialized = false;
    }

    @Override
    public void addRoute(String httpMethod, RouteImpl route) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + route.getPath() + "'", route.getAcceptType(), route);
    }

    @Override
    public void addFilter(String httpMethod, FilterImpl filter) {
        init();
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + filter.getPath() + "'", filter.getAcceptType(), filter);
    }

    public synchronized void init() {
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get();
            if (!ServletFlag.isRunningFromServlet()) {
                new Thread(() -> {
                    server = SparkServerFactory.create(hasMultipleHandlers());
                    server.ignite(
                            ipAddress,
                            port,
                            sslStores,
                            latch,
                            maxThreads,
                            minThreads,
                            threadIdleTimeoutMillis,
                            webSocketHandlers,
                            webSocketIdleTimeoutMillis);
                }).start();
            }
            initialized = true;
        }
    }

    //////////////////////////////////////////////////
    // EXCEPTION mapper
    //////////////////////////////////////////////////

    /**
     * Maps an exception handler to be executed when an exception occurs during routing
     *
     * @param exceptionClass the exception class
     * @param handler        The handler
     */
    public synchronized void exception(Class<? extends Exception> exceptionClass, ExceptionHandler handler) {
        // wrap
        ExceptionHandlerImpl wrapper = new ExceptionHandlerImpl(exceptionClass) {
            @Override
            public void handle(Exception exception, Request request, Response response) {
                handler.handle(exception, request, response);
            }
        };

        ExceptionMapper.getInstance().map(exceptionClass, wrapper);
    }

    //////////////////////////////////////////////////
    // HALT methods
    //////////////////////////////////////////////////

    /**
     * Immediately stops a request within a filter or route
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     */
    public void halt() {
        throw new HaltException();
    }

    /**
     * Immediately stops a request within a filter or route with specified status code
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status the status code
     */
    public void halt(int status) {
        throw new HaltException(status);
    }

    /**
     * Immediately stops a request within a filter or route with specified body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param body The body content
     */
    public void halt(String body) {
        throw new HaltException(body);
    }

    /**
     * Immediately stops a request within a filter or route with specified status code and body content
     * NOTE: When using this don't catch exceptions of type HaltException, or if catched, re-throw otherwise
     * halt will not work
     *
     * @param status The status code
     * @param body   The body content
     */
    public void halt(int status, String body) {
        throw new HaltException(status, body);
    }

}
