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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

import spark.routematch.RouteMatch;
import spark.utils.IOUtils;
import spark.utils.SparkUtils;
import spark.utils.StringUtils;

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

    private HttpServletRequest servletRequest;

    private Session session = null;
    private boolean validSession = false;


    /* Lazy loaded stuff */
    private String body = null;
    private byte[] bodyAsBytes = null;

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
     *
     * @param match   the route match
     * @param request the servlet request
     */
    Request(RouteMatch match, HttpServletRequest request) {
        this.servletRequest = request;
        changeMatch(match);
    }

    /**
     * Constructor - Used to create a request and no RouteMatch is available.
     *
     * @param request the servlet request
     */
    Request(HttpServletRequest request) {
        this.servletRequest = request;

        // Empty
        params = new HashMap<>();
        splat = new ArrayList<>();
    }

    protected void changeMatch(RouteMatch match) {
        List<String> requestList = SparkUtils.convertRouteToList(match.getRequestURI());
        List<String> matchedList = SparkUtils.convertRouteToList(match.getMatchUri());

        params = getParams(requestList, matchedList);
        splat = getSplat(requestList, matchedList);
    }

    /**
     * Returns the map containing all route params
     *
     * @return a map containing all route params
     */
    public Map<String, String> params() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * Returns the value of the provided route pattern parameter.
     * Example: parameter 'name' from the following pattern: (get '/hello/:name')
     *
     * @param param the param
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
     * @return an array containing the splat (wildcard) parameters
     */
    public String[] splat() {
        return splat.toArray(new String[splat.size()]);
    }

    /**
     * @return request method e.g. GET, POST, PUT, ...
     */
    public String requestMethod() {
        return servletRequest.getMethod();
    }

    /**
     * @return the scheme
     */
    public String scheme() {
        return servletRequest.getScheme();
    }

    /**
     * @return the host
     */
    public String host() {
        return servletRequest.getHeader("host");
    }

    /**
     * @return the user-agent
     */
    public String userAgent() {
        return servletRequest.getHeader(USER_AGENT);
    }

    /**
     * @return the server port
     */
    public int port() {
        return servletRequest.getServerPort();
    }


    /**
     * @return the path info
     * Example return: "/example/foo"
     */
    public String pathInfo() {
        return servletRequest.getPathInfo();
    }

    /**
     * @return the servlet path
     */
    public String servletPath() {
        return servletRequest.getServletPath();
    }

    /**
     * @return the context path
     */
    public String contextPath() {
        return servletRequest.getContextPath();
    }

    /**
     * @return the URL string
     */
    public String url() {
        return servletRequest.getRequestURL().toString();
    }

    /**
     * @return the content type of the body
     */
    public String contentType() {
        return servletRequest.getContentType();
    }

    /**
     * @return the client's IP address
     */
    public String ip() {
        return servletRequest.getRemoteAddr();
    }

    /**
     * @return the request body sent by the client
     */
    public String body() {

        if (body == null) {
            body = StringUtils.toString(bodyAsBytes(), servletRequest.getCharacterEncoding());
        }

        return body;
    }

    public byte[] bodyAsBytes() {
        if (bodyAsBytes == null) {
            readBodyAsBytes();
        }
        return bodyAsBytes;
    }

    private void readBodyAsBytes() {
        try {
            bodyAsBytes = IOUtils.toByteArray(servletRequest.getInputStream());
        } catch (Exception e) {
            LOG.warn("Exception when reading body", e);
        }
    }

    /**
     * @return the length of request.body
     */
    public int contentLength() {
        return servletRequest.getContentLength();
    }

    /**
     * Gets the query param
     *
     * @param queryParam the query parameter
     * @return the value of the provided queryParam
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     */
    public String queryParams(String queryParam) {
        return servletRequest.getParameter(queryParam);
    }

    /**
     * Gets the query param, or returns default value
     *
     * @param queryParam the query parameter
     * @param defaultValue the default value
     * @return the value of the provided queryParam, or default if value is null
     * Example: query parameter 'id' from the following request URI: /hello?id=foo
     */
    public String queryParamOrDefault(String queryParam, String defaultValue) {
        String value = queryParams(queryParam);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets all the values of the query param
     * Example: query parameter 'id' from the following request URI: /hello?id=foo&amp;id=bar
     *
     * @param queryParam the query parameter
     * @return the values of the provided queryParam, null if it doesn't exists
     */
    public String[] queryParamsValues(String queryParam) {
        return servletRequest.getParameterValues(queryParam);
    }

    /**
     * Gets the value for the provided header
     *
     * @param header the header
     * @return the value of the provided header
     */
    public String headers(String header) {
        return servletRequest.getHeader(header);
    }

    /**
     * @return all query parameters
     */
    public Set<String> queryParams() {
        return servletRequest.getParameterMap().keySet();
    }

    /**
     * @return all headers
     */
    public Set<String> headers() {
        if (headers == null) {
            headers = new TreeSet<>();
            Enumeration<String> enumeration = servletRequest.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                headers.add(enumeration.nextElement());
            }
        }
        return headers;
    }

    /**
     * @return the query string
     */
    public String queryString() {
        return servletRequest.getQueryString();
    }

    /**
     * Sets an attribute on the request (can be fetched in filters/routes later in the chain)
     *
     * @param attribute The attribute
     * @param value     The attribute value
     */
    public void attribute(String attribute, Object value) {
        servletRequest.setAttribute(attribute, value);
    }

    /**
     * Gets the value of the provided attribute
     *
     * @param attribute The attribute value or null if not present
     * @param <T>       the type parameter.
     * @return the value for the provided attribute
     */
    @SuppressWarnings("unchecked")
    public <T> T attribute(String attribute) {
        return (T) servletRequest.getAttribute(attribute);
    }


    /**
     * @return all attributes
     */
    public Set<String> attributes() {
        Set<String> attrList = new HashSet<>();
        Enumeration<String> attributes = (Enumeration<String>) servletRequest.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }

    /**
     * @return the raw HttpServletRequest object handed in by Jetty
     */
    public HttpServletRequest raw() {
        return servletRequest;
    }

    /**
     * @return the query map
     */
    public QueryParamsMap queryMap() {
        initQueryMap();

        return queryMap;
    }

    /**
     * @param key the key
     * @return the query map
     */
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
        if (session == null || !validSession) {
            validSession(true);
            session = new Session(servletRequest.getSession(), this);
        }
        return session;
    }

    /**
     * Returns the current session associated with this request, or if there is
     * no current session and <code>create</code> is true, returns  a new session.
     *
     * @param create <code>true</code> to create a new session for this request if necessary;
     *               <code>false</code> to return null if there's no current session
     * @return the session associated with this request or <code>null</code> if
     * <code>create</code> is <code>false</code> and the request has no valid session
     */
    public Session session(boolean create) {
        if (session == null || !validSession) {
            HttpSession httpSession = servletRequest.getSession(create);
            if (httpSession != null) {
                validSession(true);
                session = new Session(httpSession, this);
            } else {
                session = null;
            }
        }
        return session;
    }

    /**
     * @return request cookies (or empty Map if cookies aren't present)
     */
    public Map<String, String> cookies() {
        Map<String, String> result = new HashMap<>();
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
     *
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

    /**
     * @return the part of this request's URL from the protocol name up to the query string in the first line of the HTTP request.
     */
    public String uri() {
        return servletRequest.getRequestURI();
    }

    /**
     * @return Returns the name and version of the protocol the request uses
     */
    public String protocol() {
        return servletRequest.getProtocol();
    }

    private static Map<String, String> getParams(List<String> request, List<String> matched) {
        Map<String, String> params = new HashMap<>();

        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (SparkUtils.isParam(matchedPart)) {
                try {
                    String decodedReq = URLDecoder.decode(request.get(i), "UTF-8");
                    LOG.debug("matchedPart: "
                                      + matchedPart
                                      + " = "
                                      + decodedReq);
                    params.put(matchedPart.toLowerCase(), decodedReq);

                } catch (UnsupportedEncodingException e) {

                }
            }
        }
        return Collections.unmodifiableMap(params);
    }

    private static List<String> getSplat(List<String> request, List<String> matched) {
        int nbrOfRequestParts = request.size();
        int nbrOfMatchedParts = matched.size();

        boolean sameLength = (nbrOfRequestParts == nbrOfMatchedParts);

        List<String> splat = new ArrayList<>();

        for (int i = 0; (i < nbrOfRequestParts) && (i < nbrOfMatchedParts); i++) {

            String matchedPart = matched.get(i);

            if (SparkUtils.isSplat(matchedPart)) {

                StringBuilder splatParam = new StringBuilder(request.get(i));

                if (!sameLength && (i == (nbrOfMatchedParts - 1))) {
                    for (int j = i + 1; j < nbrOfRequestParts; j++) {
                        splatParam.append("/");
                        splatParam.append(request.get(j));
                    }
                }
                try {
                    String decodedSplat = URLDecoder.decode(splatParam.toString(), "UTF-8");
                    splat.add(decodedSplat);
                } catch (UnsupportedEncodingException e) {
                }
            }
        }

        return Collections.unmodifiableList(splat);
    }

    /**
     * Set the session validity
     *
     * @param validSession the session validity
     */
    void validSession(boolean validSession) {
        this.validSession = validSession;
    }

}
