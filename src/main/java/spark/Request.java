package spark;

import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import spark.route.HttpMethod;
import spark.utils.IOUtils;

public class Request {

    private static final String USER_AGENT = "user-agent";
    
    private HttpMethod httpMethod;
    private HttpServletRequest servletRequest;

    /* Lazy loaded stuff */
    private String body = null;
    
    private Set<String> headers = null;
    
    //  request.body              # request body sent by the client (see below), DONE
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
    Request(HttpMethod httpMethod, HttpServletRequest request) {
        this.httpMethod = httpMethod;
        this.servletRequest = request;
    }
    
    /**
     * Returns request method e.g. GET, POST, PUT, ...
     */
    public String requestMethod() {
        return httpMethod.name();
    }

    /**
     * Returns the scheme
     */
    public String scheme() {
        return servletRequest.getScheme();
    }
    
    /**
     * Returns the host
     */
    public String host() {
        return servletRequest.getHeader("host");
    }

    /**
     * Returns the user-agent
     */
    public String userAgent() {
        return servletRequest.getHeader(USER_AGENT);
    }
    
    /**
     * Returns the server port
     */
    public int port() {
        return servletRequest.getServerPort();
    }


    /**
     * Returns the path info
     * Example return: "/example/foo"
     */
    public String pathInfo() {
        return servletRequest.getPathInfo();
    }
    
    /**
     * Returns the URL string
     */
    public String url() {
        return scheme() + "://" + host() + pathInfo();
    }
    
    /**
     * Returns the content type of the body
     */
    public String contentType() {
        return servletRequest.getContentType();
    }

    /**
     * Returns the client's IP address
     */
    public String ip() {
        return servletRequest.getRemoteAddr();
    }
    
    /**
     * Returns the request body sent by the client
     */
    public String body() {
        if (body == null) {
            try {
                body = IOUtils.toString(servletRequest.getInputStream());
            } catch (Exception e) {
            }
        }
        return body;
    }
    
    /**
     * Returns the length of request.body
     */
    public int contentLength() {
        return servletRequest.getContentLength();
    }

    /**
     * Returns the value of the provided queryParam
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     */
    public String queryParam(String queryParam) {
        return servletRequest.getParameter(queryParam);
    }

    /**
     * Returns the value of the provided header
     */
    public String header(String header) {
        return servletRequest.getHeader(header);
    }

    /**
     * Returns all query parameters
     */
    @SuppressWarnings("unchecked")
    public Set<String> queryParams() {
        return servletRequest.getParameterMap().keySet();
    }

    /**
     * Returns all headers
     */
    @SuppressWarnings("unchecked")
    public Set<String> headers() {
        if (headers == null) {
            headers = new TreeSet<String>();
            Enumeration<String> enumeration = servletRequest.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                headers.add(enumeration.nextElement());
            }
        }
        return headers;
    }

    /**
     * Returns the query string
     */
    public String queryString() {
        return servletRequest.getQueryString();
    }

    /**
     * Gets the raw HttpServletRequest object handed in by Jetty
     */
    public HttpServletRequest raw() {
        return servletRequest;
    }
    
}
