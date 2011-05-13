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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.utils.IOUtils;
import spark.utils.SparkUtils;

public class WebContext {

    private static final String USER_AGENT = "user-agent";

    private static Logger LOG = Logger.getLogger(WebContext.class);

    private Map<String, String> params;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpMethod httpMethod;

    /* Lazy loaded stuff */
    private String body = null;
    private Set<String> headers = null;

    public WebContext(RouteMatch match, HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.httpMethod = match.getHttpMethod();
        params = new HashMap<String, String>();
        setParams(match);
    }

    void stuff() {
        //    request.body              # request body sent by the client (see below), DONE
        //    request.scheme            # "http"                                DONE
        //    request.script_name       # "/example"
        //    request.path_info         # "/foo",                               DONE
        //    request.port              # 80                                    DONE
        //    request.request_method    # "GET",                                DONE
        //    request.query_string      # "",                                   DONE
        //    request.content_length    # length of request.body,               DONE
        //    request.media_type        # media type of request.body            DONE, content type?
        //    request.host              # "example.com"                         DONE
        //    request.get?              # true (similar methods for other verbs)
        //    request.form_data?        # false
        //    request["SOME_HEADER"]    # value of SOME_HEADER header,          DONE
        //    request.referrer          # the referrer of the client or '/'
        //    request.user_agent        # user agent (used by :agent condition) DONE
        //    request.cookies           # hash of browser cookies
        //    request.xhr?              # is this an ajax request?
        //    request.url               # "http://example.com/example/foo"      DONE
        //    request.path              # "/example/foo"
        //    request.ip                # client IP address                     DONE
        //    request.secure?           # false (would be true over ssl)
        //    request.forwarded?        # true (if running behind a reverse proxy)
        //    request.env               # raw env hash handed in by Rack,       DONE
    }

    /**
     * Returns request method e.g. GET, POST, PUT, ...
     */
    public String getRequestMethod() {
        return httpMethod.name();
    }
    
    /**
     * Returns the scheme
     */
    public String getScheme() {
        return request.getScheme();
    }
    
    /**
     * Returns the host
     */
    public String getHost() {
        return request.getHeader("host");
    }

    /**
     * Returns the user-agent
     */
    public String getUserAgent() {
        return request.getHeader(USER_AGENT);
    }
    
    /**
     * Returns the content type of the body
     */
    public String getContentType() {
        return request.getContentType();
    }
    
    /**
     * Returns the client's IP address
     */
    public String getClientIP() {
        return request.getRemoteAddr();
    }
    
    /**
     * Returns the server port
     */
    public int getPort() {
        return request.getServerPort();
    }

    /**
     * Returns the URL string
     */
    public String getUrl() {
        return getScheme() + "://" + getHost() + getPathInfo();
    }
    
    /**
     * Returns the path info
     * Example return: "/example/foo"
     */
    public String getPathInfo() {
        return request.getPathInfo();
    }

    /**
     * Returns the request body sent by the client
     */
    public String getBody() {
        if (body == null) {
            try {
                body = IOUtils.toString(request.getInputStream());
            } catch (Exception e) {
            }
        }
        return body;
    }

    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name') 
     */
    public String getParam(String param) {
        return params.get(param);
    }

    /**
     * Returns the length of request.body
     */
    public int getContentLength() {
        return request.getContentLength();
    }

    /**
     * Returns the value of the provided queryParam
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     */
    public String getQueryParam(String queryParam) {
        return request.getParameter(queryParam);
    }

    /**
     * Returns the value of the provided header
     */
    public String getHeader(String header) {
        return request.getHeader(header);
    }

    /**
     * Returns all query parameters
     */
    @SuppressWarnings("unchecked")
    public Set<String> getQueryParams() {
        return request.getParameterMap().keySet();
    }

    /**
     * Returns all headers
     */
    @SuppressWarnings("unchecked")
    public Set<String> getHeaders() {
        if (headers == null) {
            headers = new TreeSet<String>();
            Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                headers.add(enumeration.nextElement());
            }
        }
        return headers;
    }

    /**
     * Returns the query string
     */
    public String getQueryString() {
        return request.getQueryString();
    }

    /**
     * Gets the raw request object handed in by Jetty
     */
    public HttpServletRequest getRawRequest() {
        return request;
    }

    /**
     * Gets the raw response object handed in by Jetty
     */
    public HttpServletResponse getRawResponse() {
        return response;
    }

    // TODO: implement error handling
    public void redirect(String location) {
        try {
            response.sendRedirect(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void returnType() {

    }

    public void setContentType(String type) {
        response.setContentType(type);
    }

    private final void setParams(RouteMatch match) {
        LOG.info("set params for requestUri: "
                        + match.getRequestUri()
                        + ", matchUri: "
                        + match.getMatchUri());

        List<String> request = SparkUtils.convertRouteToList(match.getRequestUri());
        List<String> matched = SparkUtils.convertRouteToList(match.getMatchUri());

        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (SparkUtils.isParam(matchedPart)) {
                LOG.info("matchedPart: "
                                + SparkUtils.getParamName(matchedPart)
                                + " = "
                                + request.get(i));
                params.put(SparkUtils.getParamName(matchedPart), request.get(i));
            }
        }
    }

}