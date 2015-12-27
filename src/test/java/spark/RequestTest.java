package spark;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.junit.Before;
import org.junit.Test;

import spark.routematch.RouteMatch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestTest {

    private static final String THE_SERVLET_PATH = "/the/servlet/path";
    private static final String THE_CONTEXT_PATH = "/the/context/path";

    HttpServletRequest servletRequest;
    HttpSession httpSession;

    RouteMatch match = new RouteMatch(null, "/hi", "/hi", "text/html");

    @Before
    public void setup() {

        servletRequest = mock(HttpServletRequest.class);
        httpSession = mock(HttpSession.class);

    }

    @Test
    public void queryParamShouldReturnsParametersFromQueryString() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("name", new String[] {"Federico"});
        HttpServletRequest servletRequest = new MockedHttpServletRequest(params);
        Request request = new Request(match, servletRequest);
        String name = request.queryParams("name");
        assertEquals("Invalid name in query string", "Federico", name);
    }

    @Test
    public void queryParamShouldBeParsedAsHashMap() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("user[name]", new String[] {"Federico"});
        HttpServletRequest servletRequest = new MockedHttpServletRequest(params);
        Request request = new Request(match, servletRequest);
        String name = request.queryMap("user").value("name");
        assertEquals("Invalid name in query string", "Federico", name);
    }

    @Test
    public void shouldBeAbleToGetTheServletPath() {
        HttpServletRequest servletRequest = new MockedHttpServletRequest(new HashMap<String, String[]>()) {
            @Override
            public String getServletPath() {
                return THE_SERVLET_PATH;
            }
        };
        Request request = new Request(match, servletRequest);
        assertEquals("Should have delegated getting the servlet path", THE_SERVLET_PATH, request.servletPath());
    }

    @Test
    public void shouldBeAbleToGetTheContextPath() {
        HttpServletRequest servletRequest = new MockedHttpServletRequest(new HashMap<String, String[]>()) {
            @Override
            public String getContextPath() {
                return THE_CONTEXT_PATH;
            }
        };
        Request request = new Request(match, servletRequest);
        assertEquals("Should have delegated getting the context path", THE_CONTEXT_PATH, request.contextPath());
    }

    @Test
    public void testSessionNoParams_whenSessionIsNull() {

        when(servletRequest.getSession()).thenReturn(httpSession);

        Request request = new Request(match, servletRequest);

        assertEquals("A Session with an HTTPSession from the Request should have been created",
                httpSession, request.session().raw());
    }

    @Test
    public void testSession_whenCreateIsTrue() {

        when(servletRequest.getSession(true)).thenReturn(httpSession);

        Request request = new Request(match, servletRequest);

        assertEquals("A Session with an HTTPSession from the Request should have been created because create parameter " +
                        "was set to true",
                httpSession, request.session(true).raw());

    }

    @Test
    public void testSession_whenCreateIsFalse() {

        when(servletRequest.getSession(true)).thenReturn(httpSession);

        Request request = new Request(match, servletRequest);

        assertEquals("A Session should not have been created because create parameter was set to false",
                null, request.session(false));

    }

    @Test
    public void testCookies_whenCookiesArePresent() {

        Collection<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie("cookie1", "cookie1value"));
        cookies.add(new Cookie("cookie2", "cookie2value"));

        Map<String, String> expected = new HashMap<>();
        for(Cookie cookie : cookies) {
            expected.put(cookie.getName(), cookie.getValue());
        }

        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        Cookie[] cookieArray = cookies.toArray(new Cookie[cookies.size()]);

        when(servletRequest.getCookies()).thenReturn(cookieArray);

        Request request = new Request(match, servletRequest);

        assertTrue("The count of cookies returned should be the same as those in the request",
                request.cookies().size() == 2);

        assertEquals("A Map of Cookies should have been returned because they exist", expected, request.cookies());

    }

    @Test
    public void testCookies_whenCookiesAreNotPresent() {

        when(servletRequest.getCookies()).thenReturn(null);

        Request request = new Request(match, servletRequest);

        assertNotNull("A Map of Cookies should have been instantiated even if cookies are not present in the request",
                request.cookies());

        assertTrue("The Map of cookies should be empty because cookies are not present in the request",
                request.cookies().size() == 0);

    }

    @Test
    public void testCookie_whenCookiesArePresent() {

        final String cookieKey = "cookie1";
        final String cookieValue = "cookie1value";

        Collection<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie(cookieKey, cookieValue));

        Cookie[] cookieArray = cookies.toArray(new Cookie[cookies.size()]);
        when(servletRequest.getCookies()).thenReturn(cookieArray);

        Request request = new Request(match, servletRequest);

        assertNotNull("A value for the key provided should exist because a cookie with the same key is present",
                request.cookie(cookieKey));

        assertEquals("The correct value for the cookie key supplied should be returned",
                cookieValue, request.cookie(cookieKey));

    }

    @Test
    public void testCookie_whenCookiesAreNotPresent() {

        final String cookieKey = "nonExistentCookie";

        when(servletRequest.getCookies()).thenReturn(null);

        Request request = new Request(match, servletRequest);

        assertNull("A null value should have been returned because the cookie with that key does not exist",
                request.cookie(cookieKey));

    }

    @Test
    public void testRequestMethod() {

        final String requestMethod = "GET";

        when(servletRequest.getMethod()).thenReturn(requestMethod);

        Request request = new Request(match, servletRequest);

        assertEquals("The request method of the underlying servlet request should be returned",
                requestMethod, request.requestMethod());

    }

    @Test
    public void testScheme() {

        final String scheme = "http";

        when(servletRequest.getScheme()).thenReturn(scheme);

        Request request = new Request(match, servletRequest);

        assertEquals("The scheme of the underlying servlet request should be returned",
                scheme, request.scheme());

    }

    @Test
    public void testHost() {

        final String host = "www.google.com";

        when(servletRequest.getHeader("host")).thenReturn(host);

        Request request = new Request(match, servletRequest);

        assertEquals("The value of the host header of the underlying servlet request should be returned",
                host, request.host());

    }

    @Test
    public void testUserAgent() {

        final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";

        when(servletRequest.getHeader("user-agent")).thenReturn(userAgent);

        Request request = new Request(match, servletRequest);

        assertEquals("The value of the user agent header of the underlying servlet request should be returned",
                userAgent, request.userAgent());

    }

    @Test
    public void testPort() {

        final int port = 80;

        when(servletRequest.getServerPort()).thenReturn(80);

        Request request = new Request(match, servletRequest);

        assertEquals("The server port of the the underlying servlet request should be returned",
                port, request.port());

    }

    @Test
    public void testPathInfo() {

        final String pathInfo = "/path/to/resource";

        when(servletRequest.getPathInfo()).thenReturn(pathInfo);

        Request request = new Request(match, servletRequest);

        assertEquals("The path info of the underlying servlet request should be returned",
                pathInfo, request.pathInfo());

    }

    @Test
    public void testServletPath() {

        final String servletPath = "/api";

        when(servletRequest.getServletPath()).thenReturn(servletPath);

        Request request = new Request(match, servletRequest);

        assertEquals("The servlet path of the underlying servlet request should be returned",
                servletPath, request.servletPath());

    }

    @Test
    public void testContextPath() {

        final String contextPath = "/my-app";

        when(servletRequest.getContextPath()).thenReturn(contextPath);

        Request request = new Request(match, servletRequest);

        assertEquals("The context path of the underlying servlet request should be returned",
                contextPath, request.contextPath());

    }

    @Test
    public void testUrl() {

        final String url = "http://www.myapp.com/myapp/a";

        when(servletRequest.getRequestURL()).thenReturn(new StringBuffer(url));

        Request request = new Request(match, servletRequest);

        assertEquals("The request url of the underlying servlet request should be returned",
                url, request.url());

    }

    @Test
    public void testContentType() {

        final String contentType = "image/jpeg";

        when(servletRequest.getContentType()).thenReturn(contentType);

        Request request = new Request(match, servletRequest);

        assertEquals("The content type of the underlying servlet request should be returned",
                contentType, request.contentType());

    }

    @Test
    public void testIp() {

        final String ip = "216.58.197.106:80";

        when(servletRequest.getRemoteAddr()).thenReturn(ip);

        Request request = new Request(match, servletRequest);

        assertEquals("The remote IP of the underlying servlet request should be returned",
                ip, request.ip());

    }

    @Test
    public void testContentLength() {

        final int contentLength = 500;

        when(servletRequest.getContentLength()).thenReturn(contentLength);

        Request request = new Request(match, servletRequest);

        assertEquals("The content length the underlying servlet request should be returned",
                contentLength, request.contentLength());

    }

    @Test
    public void testHeaders() {

        final String headerKey = "host";
        final String host = "www.google.com";

        when(servletRequest.getHeader(headerKey)).thenReturn(host);

        Request request = new Request(match, servletRequest);

        assertEquals("The value of the header specified should be returned",
                host, request.headers(headerKey));

    }

    @Test
    public void testQueryParamsValues_whenParamExists() {

        final String[] paramValues = {"foo", "bar"};

        when(servletRequest.getParameterValues("id")).thenReturn(paramValues);

        Request request = new Request(match, servletRequest);

        assertArrayEquals("An array of Strings for a parameter with multiple values should be returned",
                paramValues, request.queryParamsValues("id"));

    }

    @Test
    public void testQueryParamsValues_whenParamDoesNotExists() {

        when(servletRequest.getParameterValues("id")).thenReturn(null);

        Request request = new Request(match, servletRequest);

        assertNull("Null should be returned because the parameter specified does not exist in the request",
                request.queryParamsValues("id"));

    }

    @Test
    public void testQueryParams() {

        Map<String, String[]> params = new HashMap<>();
        params.put("sort", new String[]{"asc"});
        params.put("items", new String[]{"10"});

        when(servletRequest.getParameterMap()).thenReturn(params);

        Request request = new Request(match, servletRequest);

        Set<String> result = request.queryParams();

        assertArrayEquals("Should return the query parameter names", params.keySet().toArray(), result.toArray());

    }

    @Test
    public void testURI() {

        final String requestURI = "http://localhost:8080/myapp/";

        when(servletRequest.getRequestURI()).thenReturn(requestURI);

        Request request = new Request(match, servletRequest);

        assertEquals("The request URI should be returned",
                requestURI, request.uri());

    }

    @Test
    public void testProtocol() {

        final String protocol = "HTTP/1.1";

        when(servletRequest.getProtocol()).thenReturn(protocol);

        Request request = new Request(match, servletRequest);

        assertEquals("The underlying request protocol should be returned",
                protocol, request.protocol());

    }


    public static class MockedHttpServletRequest implements HttpServletRequest {
        private Map<String, String[]> params;

        public MockedHttpServletRequest(Map<String, String[]> params) {
            this.params = params;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return null;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        @Deprecated
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return this.params.get(name)[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return this.params;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return null;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        @Deprecated
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public void removeAttribute(String name) {
            // do nothing
        }

        @Override
        public void setAttribute(String name, Object o) {
            // do nothing
        }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
            // do nothing
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest,
                                       ServletResponse servletResponse) throws IllegalStateException {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }

        @Override
        public boolean authenticate(HttpServletResponse response)
                throws IOException, ServletException {
            return false;
        }

        @Override
        public void login(String username, String password)
                throws ServletException {
            // do nothing
        }

        @Override
        public void logout() throws ServletException {
            // do nothing
        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return null;
        }

        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return null;
        }

        @Override
        public long getContentLengthLong() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String changeSessionId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
                throws IOException, ServletException {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
