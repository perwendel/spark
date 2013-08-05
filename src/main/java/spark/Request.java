/*
 * Copyright 2011- Per Wendel
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.utils.IOUtils;
import spark.utils.SparkUtils;

/**
 * Provides information about the HTTP request
 *
 * @author Per Wendel
 */
public class Request {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Request.class);
    
    private static final String USER_AGENT = "user-agent";
    
    private Map<String, String> params;
    private List<String> splat;
    private QueryParamsMap queryMap;
    
    private HttpMethod httpMethod;
    private HttpServletRequest servletRequest;

    private Session session = null;
    
    /* Lazy loaded stuff */
    private String body = null;
    
    private Set<String> headers = null;
    
    //    request.body              # request body sent by the client (see below), DONE
    //    request.scheme            # "http"                                DONE
    //    request.path_info         # "/foo",                               DONE
    //    request.port              # 80                                    DONE
    //    request.request_method    # "GET",                                DONE
    //    request.query_string      # "",                                   DONE
    //    request.content_length    # length of request.body,               DONE
    //    request.media_type        # media type of request.body            DONE, content type?
    //    request.host              # "example.com"                         DONE
    //    request["SOME_HEADER"]    # value of SOME_HEADER header,          DONE
    //    request.user_agent        # user agent (used by :agent condition) DONE
    //    request.url               # "http://example.com/example/foo"      DONE
    //    request.ip                # client IP address                     DONE
    //    request.env               # raw env hash handed in by Rack,       DONE
    //    request.get?              # true (similar methods for other verbs)
    //    request.secure?           # false (would be true over ssl)
    //    request.forwarded?        # true (if running behind a reverse proxy)
    //    request.cookies           # hash of browser cookies,              DONE
    //    request.xhr?              # is this an ajax request?
    //    request.script_name       # "/example"
    //    request.form_data?        # false
    //    request.referrer          # the referrer of the client or '/'
    
    protected Request() {
       // Used by wrapper
    }
    
    /**
     * Constructor
     */
    Request(RouteMatch match, HttpServletRequest request) {
        this.httpMethod = match.getHttpMethod();
        this.servletRequest = request;
        
        List<String> requestList = SparkUtils.convertRouteToList(match.getRequestURI());
        List<String> matchedList = SparkUtils.convertRouteToList(match.getMatchUri());
        
        params = getParams(requestList, matchedList);
        splat = getSplat(requestList, matchedList);
    }
    
    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name')
     * 
     * @return null if the given param is null or not found 
     */
    public String params(String param) {
        if (param == null) {
            return null;
        }

        if (param.startsWith(":")) {
            return params.get(param.toLowerCase()); // NOSONAR
        } else {
            return params.get(":" + param.toLowerCase()); // NOSONAR
        }
    }
    
    /**
     * Returns an arrat containing the splat (wildcard) parameters 
     */
    public String[] splat() {
        return splat.toArray(new String[splat.size()]);
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
     * Returns the servlet path
     */
    public String servletPath() {
        return servletRequest.getServletPath();
    }
    
    /**
     * Returns the URL string
     */
    public String url() {
    	return servletRequest.getRequestURL().toString();
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
                LOG.warn("Exception when reading body", e);
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
    public String queryParams(String queryParam) {
        return servletRequest.getParameter(queryParam);
    }

    /**
     * Returns the value of the provided header
     */
    public String headers(String header) {
        return servletRequest.getHeader(header);
    }

    /**
     * Returns all query parameters
     */
    public Set<String> queryParams() {
        return servletRequest.getParameterMap().keySet();
    }

    /**
     * Returns all headers
     */
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
     * Sets an attribute on the request (can be fetched in filters/routes later in the chain)
     * @param attribute The attribute
     * @param value The attribute value
     */
    public void attribute(String attribute, Object value) {
        servletRequest.setAttribute(attribute, value);
    }
    
    /**
     * Gets the value of the provided attribute
     * @param attribute The attribute value or null if not present
     */
    public Object attribute(String attribute) {
        return servletRequest.getAttribute(attribute);
    }
    
    
    /**
     * Returns all attributes
     */
    public Set<String> attributes() {
        Set<String> attrList = new HashSet<String>();
        Enumeration<String> attributes = (Enumeration<String>) servletRequest.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }
    
    /**
     * Gets the raw HttpServletRequest object handed in by Jetty
     */
    public HttpServletRequest raw() {
        return servletRequest;
    }
    
    public QueryParamsMap queryMap() {
        initQueryMap();
        
        return queryMap;
    }
    
    public QueryParamsMap queryMap(String key) {
        return queryMap().get(key);
    }

    private void initQueryMap() {
        if (queryMap == null) {
            queryMap = new QueryParamsMap(raw());
        }
    }
    
    /**
     * Returns the current session associated with this request, 
     * or if the request does not have a session, creates one.
     *  
     * @return the session associated with this request
     */
    public Session session() {
        if (session == null) {
            session = new Session(servletRequest.getSession());
        }
        return session;
    }

    /**
     * Returns the current session associated with this request, or if there is 
     * no current session and <code>create</code> is true, returns  a new session.
     * 
     * @param create <code>true</code> to create a new session for this request if necessary;
     *              <code>false</code> to return null if there's no current session 
     * @return the session associated with this request or <code>null</code> if
     *          <code>create</code> is <code>false</code> and the request has no valid session
     */
    public Session session(boolean create) {
        if (session == null) {
            HttpSession httpSession = servletRequest.getSession(create);
            if (httpSession != null) {
                session = new Session(httpSession);
            }
        }
        return session;
    }
    
    /**
     * @return request cookies (or empty Map if cookies dosn't present)
     */
    public Map<String, String> cookies() {
        Map<String, String> result = new HashMap<String, String>();
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                result.put(cookie.getName(), cookie.getValue());
            }
        }
        return result;
    }
    
    /**
     * Gets cookie by name.
     * @param name name of the cookie
     * @return cookie value or null if the cookie was not found
     */
    public String cookie(String name) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    private static Map<String, String> getParams(List<String> request, List<String> matched) {
        LOG.debug("get params");

        Map<String, String> params = new HashMap<String, String>();
        
        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (SparkUtils.isParam(matchedPart)) {
                LOG.debug("matchedPart: "
                                + matchedPart
                                + " = "
                                + request.get(i));
                params.put(matchedPart.toLowerCase(), request.get(i));
            }
        }
        return Collections.unmodifiableMap(params);
    }
    
    private static List<String> getSplat(List<String> request, List<String> matched) {
        LOG.debug("get splat");

        int nbrOfRequestParts = request.size();
        int nbrOfMatchedParts = matched.size();
        
        boolean sameLength = (nbrOfRequestParts == nbrOfMatchedParts);
        
        List<String> splat = new ArrayList<String>();
        
        for (int i = 0; (i < nbrOfRequestParts) && (i < nbrOfMatchedParts); i++) {
            String matchedPart = matched.get(i);
            
            if (SparkUtils.isSplat(matchedPart)) {
                
                StringBuilder splatParam = new StringBuilder(request.get(i));
                if (!sameLength && (i == (nbrOfMatchedParts -1))) {
                    for (int j = i + 1; j < nbrOfRequestParts; j++) {
                        splatParam.append("/");
                        splatParam.append(request.get(j));
                    }
                }
                splat.add(splatParam.toString());
            }
        }
        return Collections.unmodifiableList(splat);
    }
    
}
