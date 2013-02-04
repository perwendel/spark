package spark;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Test;

import spark.route.HttpMethod;
import spark.route.RouteMatch;

public class RequestTest {
    
    RouteMatch match =  new RouteMatch(HttpMethod.get,null,"/hi","/hi"); 

    @Test
    public void queryParamShouldReturnsParametersFromQueryString() {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("name",new String[] {"Federico"});
        HttpServletRequest servletRequest = new MockedHttpServletRequest(params);
        Request request = new Request(match,servletRequest);
        String name = request.queryParams("name");
        assertEquals("Invalid name in query string","Federico",name);
    }
    
    @Test
    public void queryParamShouldBeParsedAsHashMap() {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("user[name]",new String[] {"Federico"});
        HttpServletRequest servletRequest = new MockedHttpServletRequest(params);
        Request request = new Request(match,servletRequest);
        String name = request.queryMap("user").value("name");
        assertEquals("Invalid name in query string","Federico",name);
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
        public Enumeration<?> getHeaderNames() {
            return null;
        }

        @Override
        public Enumeration<?> getHeaders(String name) {
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
        public boolean isRequestedSessionIdFromUrl() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isUserInRole(String role) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Object getAttribute(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Enumeration<?> getAttributeNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getContentLength() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String getContentType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getLocalAddr() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getLocalName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getLocalPort() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public Locale getLocale() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Enumeration<?> getLocales() {
            // TODO Auto-generated method stub
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
        public Enumeration<?> getParameterNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProtocol() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getRealPath(String path) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getRemoteAddr() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getRemoteHost() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getRemotePort() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getScheme() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getServerName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getServerPort() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isSecure() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void removeAttribute(String name) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setAttribute(String name, Object o) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
            // TODO Auto-generated method stub
            
        }
        
    }
}
