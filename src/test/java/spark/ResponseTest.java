package spark;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ResponseTest {

    public Response response = new Response();
    public HttpServletResponse httpServletResponse;

    @Before
    public void setup(){
        httpServletResponse = new HttpServletResponseMock();
        response = new Response(httpServletResponse);
    }

    @Test
    public void testConstructor_whenHttpServletResponseParameter(){
        HttpServletResponse returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);
        assertEquals("Should be the same hashcode for httpServletResponse and returnResponse objects", httpServletResponse.hashCode(), returnResponse.hashCode());
        assertSame("Should be the same the HttpServletResponse object for httpServletResponse and returnResponse", httpServletResponse, returnResponse);
    }

    @Test
    public void testStatus(){
        int finalStatusCode = HttpServletResponse.SC_OK;

        response.status(finalStatusCode);
        HttpServletResponse returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);
        assertEquals("Should return status code specified", finalStatusCode, returnResponse.getStatus());
    }

    @Test
    public void testType(){
        String finalType = "text/html";

        response.type(finalType);
        HttpServletResponse returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);
        assertEquals("Should return content type specified", finalType, returnResponse.getContentType());
    }

    @Test
    public void testSetBody(){
        String finalBody = "Hello world!";

        response.body(finalBody);
        String returnBody = Whitebox.getInternalState(response, "body");
        assertNotNull("Should return a body because we configured it to have one", returnBody);
        assertEquals("Should return body specified", finalBody, returnBody);
    }

    @Test
    public void testGetBody(){
        String finalBody = "Hello world!";

        Whitebox.setInternalState(response, "body", finalBody);
        String returnBody = response.body();
        assertNotNull("Should return a body because we configured it to have one", returnBody);
        assertEquals("Should return body specified", finalBody, returnBody);
    }

    @Test
    public void testRaw(){
        HttpServletResponse returnResponse = response.raw();
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);
        assertEquals("Should be the same hashcode for httpServletResponse and returnResponse objects", httpServletResponse.hashCode(), returnResponse.hashCode());
        assertSame("Should be the same the HttpServletResponse object for httpServletResponse and returnResponse", httpServletResponse, returnResponse);
    }

    @Test
    public void testHeader(){
        String finalHeaderKey = "Content-Length";
        String finalHeaderValue = "32";

        response.header(finalHeaderKey,  finalHeaderValue);
        HttpServletResponse returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);

        String returnValue = returnResponse.getHeader(finalHeaderKey);
        assertNotNull("Should return a header value because we configured it to have one", returnValue);
        assertEquals("Should return header value specified", finalHeaderValue, returnValue);
    }

    @Test
    public void testCookie_whenNameAndValueParameters_shouldAddCookieSuccessfully(){
        String finalPath = "";
        String finalName = "cookie_name";
        String finalValue = "Test Cookie";
        int finalMaxAge = -1;
        boolean finalSecured = false;
        boolean finalHttpOnly = false;

        response.cookie(finalName, finalValue);
        HttpServletResponseMock returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponseMock because we configured it to have one", returnResponse);

        List<Cookie> returnCookies = returnResponse.getCookies();
        assertNotNull("Should return a list of cookies because we configured it to have one", returnCookies);
        assertTrue("Should have at least 1 cookie in the list of cookies", returnCookies.size() > 0);

        Cookie finalCookie = null;
        for(Cookie cookie: returnCookies){
            if(cookie.getName().equals(finalName)){
                finalCookie = cookie;
            }
        }
        assertNotNull("Should return a Cookie because we configured it to have one", finalCookie);
        assertEquals("Should return cookie path specified", finalPath, finalCookie.getPath());
        assertEquals("Should return cookie value specified", finalValue, finalCookie.getValue());
        assertEquals("Should return cookie max age specified", finalMaxAge, finalCookie.getMaxAge());
        assertEquals("Should return cookie secure specified", finalSecured, finalCookie.getSecure());
        assertEquals("Should return cookie http only specified", finalHttpOnly, finalCookie.isHttpOnly());
    }

    @Test
    public void testCookie_whenNameValueAndMaxAgeParameters_shouldAddCookieSuccessfully(){
        String finalPath = "";
        String finalName = "cookie_name";
        String finalValue = "Test Cookie";
        int finalMaxAge = 86400;
        boolean finalSecured = false;
        boolean finalHttpOnly = false;

        response.cookie(finalName, finalValue, finalMaxAge);
        HttpServletResponseMock returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponseMock because we configured it to have one", returnResponse);

        List<Cookie> returnCookies = returnResponse.getCookies();
        assertNotNull("Should return a list of cookies because we configured it to have one", returnCookies);
        assertTrue("Should have at least 1 cookie in the list of cookies", returnCookies.size() > 0);

        Cookie finalCookie = null;
        for(Cookie cookie: returnCookies){
            if(cookie.getName().equals(finalName)){
                finalCookie = cookie;
            }
        }
        assertNotNull("Should return a Cookie because we configured it to have one", finalCookie);
        assertEquals("Should return cookie path specified", finalPath, finalCookie.getPath());
        assertEquals("Should return cookie value specified", finalValue, finalCookie.getValue());
        assertEquals("Should return cookie max age specified", finalMaxAge, finalCookie.getMaxAge());
        assertEquals("Should return cookie secure specified", finalSecured, finalCookie.getSecure());
        assertEquals("Should return cookie http only specified", finalHttpOnly, finalCookie.isHttpOnly());
    }

    @Test
    public void testCookie_whenNameValueMaxAgeAndSecuredParameters_shouldAddCookieSuccessfully(){
        String finalPath = "";
        String finalName = "cookie_name";
        String finalValue = "Test Cookie";
        int finalMaxAge = 86400;
        boolean finalSecured = true;
        boolean finalHttpOnly = false;

        response.cookie(finalName, finalValue, finalMaxAge, finalSecured);
        HttpServletResponseMock returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);

        List<Cookie> returnCookies = returnResponse.getCookies();
        assertNotNull("Should return a list of cookies because we configured it to have one", returnCookies);
        assertTrue("Should have at least 1 cookie in the list of cookies", returnCookies.size() > 0);

        Cookie finalCookie = null;
        for(Cookie cookie: returnCookies){
            if(cookie.getName().equals(finalName)){
                finalCookie = cookie;
            }
        }
        assertNotNull("Should return a Cookie because we configured it to have one", finalCookie);
        assertEquals("Should return cookie path specified", finalPath, finalCookie.getPath());
        assertEquals("Should return cookie value specified", finalValue, finalCookie.getValue());
        assertEquals("Should return cookie max age specified", finalMaxAge, finalCookie.getMaxAge());
        assertEquals("Should return cookie secure specified", finalSecured, finalCookie.getSecure());
        assertEquals("Should return cookie http only specified", finalHttpOnly, finalCookie.isHttpOnly());
    }

    @Test
    public void testCookie_whenNameValueMaxAgeSecuredAndHttpOnlyParameters_shouldAddCookieSuccessfully(){
        String finalPath = "";
        String finalName = "cookie_name";
        String finalValue = "Test Cookie";
        int finalMaxAge = 86400;
        boolean finalSecured = true;
        boolean finalHttpOnly = true;

        response.cookie(finalName, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
        HttpServletResponseMock returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);

        List<Cookie> returnCookies = returnResponse.getCookies();
        assertNotNull("Should return a list of cookies because we configured it to have one", returnCookies);
        assertTrue("Should have at least 1 cookie in the list of cookies", returnCookies.size() > 0);

        Cookie finalCookie = null;
        for(Cookie cookie: returnCookies){
            if(cookie.getName().equals(finalName)){
                finalCookie = cookie;
            }
        }
        assertNotNull("Should return a Cookie because we configured it to have one", finalCookie);
        assertEquals("Should return cookie path specified", finalPath, finalCookie.getPath());
        assertEquals("Should return cookie value specified", finalValue, finalCookie.getValue());
        assertEquals("Should return cookie max age specified", finalMaxAge, finalCookie.getMaxAge());
        assertEquals("Should return cookie secure specified", finalSecured, finalCookie.getSecure());
        assertEquals("Should return cookie http only specified", finalHttpOnly, finalCookie.isHttpOnly());
    }

    @Test
    public void testCookie_whenPathNameValueMaxAgeAndSecuredParameters_shouldAddCookieSuccessfully(){
        String finalPath = "/cookie/SetCookie";
        String finalName = "cookie_name";
        String finalValue = "Test Cookie";
        int finalMaxAge = 86400;
        boolean finalSecured = true;
        boolean finalHttpOnly = false;

        response.cookie(finalPath, finalName, finalValue, finalMaxAge, finalSecured);
        HttpServletResponseMock returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);

        List<Cookie> returnCookies = returnResponse.getCookies();
        assertNotNull("Should return a list of cookies because we configured it to have one", returnCookies);
        assertTrue("Should have at least 1 cookie in the list of cookies", returnCookies.size() > 0);

        Cookie finalCookie = null;
        for(Cookie cookie: returnCookies){
            if(cookie.getName().equals(finalName)){
                finalCookie = cookie;
            }
        }
        assertNotNull("Should return a Cookie because we configured it to have one", finalCookie);
        assertEquals("Should return cookie path specified", finalPath, finalCookie.getPath());
        assertEquals("Should return cookie value specified", finalValue, finalCookie.getValue());
        assertEquals("Should return cookie max age specified", finalMaxAge, finalCookie.getMaxAge());
        assertEquals("Should return cookie secure specified", finalSecured, finalCookie.getSecure());
        assertEquals("Should return cookie http only specified", finalHttpOnly, finalCookie.isHttpOnly());
    }

    @Test
    public void testCookie_whenPathNameValueMaxAgeSecuredAndHttpOnlyParameters_shouldAddCookieSuccessfully(){
        String finalPath = "/cookie/SetCookie";
        String finalName = "cookie_name";
        String finalValue = "Test Cookie";
        int finalMaxAge = 86400;
        boolean finalSecured = true;
        boolean finalHttpOnly = true;

        response.cookie(finalPath, finalName, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
        HttpServletResponseMock returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);

        List<Cookie> returnCookies = returnResponse.getCookies();
        assertNotNull("Should return a list of cookies because we configured it to have one", returnCookies);
        assertTrue("Should have at least 1 cookie in the list of cookies", returnCookies.size() > 0);

        Cookie finalCookie = null;
        for(Cookie cookie: returnCookies){
            if(cookie.getName().equals(finalName)){
                finalCookie = cookie;
            }
        }
        assertNotNull("Should return a Cookie because we configured it to have one", finalCookie);
        assertEquals("Should return cookie path specified", finalPath, finalCookie.getPath());
        assertEquals("Should return cookie value specified", finalValue, finalCookie.getValue());
        assertEquals("Should return cookie max age specified", finalMaxAge, finalCookie.getMaxAge());
        assertEquals("Should return cookie secure specified", finalSecured, finalCookie.getSecure());
        assertEquals("Should return cookie http only specified", finalHttpOnly, finalCookie.isHttpOnly());
    }

    @Test
    public void testRemoveCookie_shouldModifyPropertiesFromCookieSuccessfully(){
        String finalPath = "/cookie/SetCookie";
        String finalName = "cookie_name";
        String finalValue = "Test Cookie";
        int finalMaxAge = 86400;
        boolean finalSecured = true;
        boolean finalHttpOnly = true;

        response.cookie(finalPath, finalName, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
        HttpServletResponseMock returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);

        List<Cookie> returnCookies = returnResponse.getCookies();
        assertNotNull("Should return a list of cookies because we configured it to have one", returnCookies);
        assertTrue("Should have at least 1 cookie in the list of cookies", returnCookies.size() > 0);

        Cookie finalCookieAdded = null;
        for(Cookie cookie: returnCookies){
            if(cookie.getName().equals(finalName)){
                finalCookieAdded = cookie;
            }
        }
        assertNotNull("Should return a Cookie because we configured it to have one", finalCookieAdded);
        assertEquals("Should return cookie path specified", finalPath, finalCookieAdded.getPath());
        assertEquals("Should return cookie value specified", finalValue, finalCookieAdded.getValue());
        assertEquals("Should return cookie max age specified", finalMaxAge, finalCookieAdded.getMaxAge());
        assertEquals("Should return cookie secure specified", finalSecured, finalCookieAdded.getSecure());
        assertEquals("Should return cookie http only specified", finalHttpOnly, finalCookieAdded.isHttpOnly());

        response.removeCookie(finalName);
        Cookie finalCookieRemoved = null;
        for(Cookie cookie: returnCookies){
            if(cookie.getName().equals(finalName)){
                finalCookieRemoved = cookie;
            }
        }
        assertNotNull("Should return a Cookie because we configured it to have one", finalCookieRemoved);
        assertEquals("Should return cookie path specified", finalPath, finalCookieRemoved.getPath());
        assertEquals("Should return cookie value specified", finalValue, finalCookieRemoved.getValue());
        assertEquals("Should return cookie max age specified", finalMaxAge, finalCookieRemoved.getMaxAge());
        assertEquals("Should return cookie secure specified", finalSecured, finalCookieRemoved.getSecure());
        assertEquals("Should return cookie http only specified", finalHttpOnly, finalCookieRemoved.isHttpOnly());
        assertNotSame("Should not be the same the cookie added and cookie removed", finalCookieAdded, finalCookieRemoved);
    }

    @Test
    public void testRedirect_whenLocationParameter_shouldModifyStatusCodeSuccessfully(){
        String finalLocation = "/test";
        int finalStatusCode = HttpServletResponse.SC_MOVED_TEMPORARILY;

        response.redirect(finalLocation);
        HttpServletResponse returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);
        assertEquals("Should return status code specified", finalStatusCode, returnResponse.getStatus());
    }

    @Test
    public void testRedirect_whenLocationAndHttpStatusCodeParameters_shouldModifyStatusCodeSuccessfully(){
        String finalLocation = "/test";
        int finalStatusCode = HttpServletResponse.SC_BAD_GATEWAY;

        response.redirect(finalLocation, finalStatusCode);
        HttpServletResponse returnResponse = Whitebox.getInternalState(response, "response");
        assertNotNull("Should return a HttpServletResponse because we configured it to have one", returnResponse);
        assertEquals("Should return status code specified", finalStatusCode, returnResponse.getStatus());
    }

    public static class HttpServletResponseMock implements HttpServletResponse {

        private int statusCode;
        private String contentType;
        private Map<String, String> headerAttributes;
        private List<Cookie> cookies = new ArrayList<Cookie>();

        @Override
        public void addCookie(Cookie cookie) {
            if(cookies == null){
                cookies = new ArrayList<>();
            }
            cookies.add(cookie);
        }

        public List<Cookie> getCookies(){
            return cookies;
        }

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public String encodeURL(String url) {
            return null;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return null;
        }

        @Override
        public String encodeUrl(String url) {
            return null;
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return null;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            setStatus(sc, msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            sendError(sc, "Test Error");
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            if (location == null) {
                throw new IllegalArgumentException();
            }
            sendError(HttpServletResponse.SC_MOVED_TEMPORARILY, location);
        }

        @Override
        public void setDateHeader(String name, long date) {
        }

        @Override
        public void addDateHeader(String name, long date) {
        }

        @Override
        public void setHeader(String name, String value) {
            if(headerAttributes == null){
                headerAttributes = new HashMap<>();
            }
            headerAttributes.put(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            if(headerAttributes == null){
                headerAttributes = new HashMap<>();
            }
            headerAttributes.put(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
        }

        @Override
        public void addIntHeader(String name, int value) {
        }

        @Override
        public void setStatus(int sc) {
            this.statusCode = sc;
        }

        @Override
        public void setStatus(int sc, String sm) {
            this.statusCode = sc;
        }

        @Override
        public int getStatus() {
            return statusCode;
        }

        @Override
        public String getHeader(String name) {
            return headerAttributes.get(name);
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Collection<String> getHeaderNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public void setContentLengthLong(long len) {
        }

        @Override
        public void setContentType(String type) {
            this.contentType = type;
        }

        @Override
        public void setBufferSize(int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() throws IOException {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(Locale loc) {
        }

        @Override
        public Locale getLocale() {
            return null;
        }
    }
}