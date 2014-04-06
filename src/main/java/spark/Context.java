package spark;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by jam on 6/04/14.
 */
public class Context {
    public final Request request;
    public final Response response;

    public Context (final Request aRequest, final Response aResponse) {
        request = aRequest;
        response = aResponse;
    }

    public Map<String, String> params () {
        return request.params ();
    }

    public String cookie (String name) {
        return request.cookie (name);
    }

    public Session session () {
        return request.session ();
    }

    public int port () {
        return request.port ();
    }

    public Object attribute (String attribute) {
        return request.attribute (attribute);
    }

    public String queryString () {
        return request.queryString ();
    }

    public String userAgent () {
        return request.userAgent ();
    }

    public QueryParamsMap queryMap () {
        return request.queryMap ();
    }

    public Set<String> queryParams () {
        return request.queryParams ();
    }

    public Set<String> attributes () {
        return request.attributes ();
    }

    public void attribute (String attribute, Object value) {
        request.attribute (attribute, value);
    }

    public Session session (boolean create) {
        return request.session (create);
    }

    public String pathInfo () {
        return request.pathInfo ();
    }

    public String contextPath () {
        return request.contextPath ();
    }

    public String servletPath () {
        return request.servletPath ();
    }

    public String params (String param) {
        return request.params (param);
    }

    public String host () {
        return request.host ();
    }

    public String scheme () {
        return request.scheme ();
    }

    public String headers (String header) {
        return request.headers (header);
    }

    public String contentType () {
        return request.contentType ();
    }

    public Set<String> headers () {
        return request.headers ();
    }

    public Map<String, String> cookies () {
        return request.cookies ();
    }

    public int contentLength () {
        return request.contentLength ();
    }

    public String url () {
        return request.url ();
    }

    public String[] splat () {
        return request.splat ();
    }

    public String queryParams (String queryParam) {
        return request.queryParams (queryParam);
    }

    public QueryParamsMap queryMap (String key) {
        return request.queryMap (key);
    }

    public String ip () {
        return request.ip ();
    }

    public HttpServletRequest requestRaw () {
        return request.raw ();
    }

    public String requestBody () {
        return request.body ();
    }

    public String requestMethod () {
        return request.requestMethod ();
    }

    public void status (int statusCode) {
        response.status (statusCode);
    }

    public void cookie (String name, String value, int maxAge) {
        response.cookie (name, value, maxAge);
    }

    public void removeCookie (String name) {
        response.removeCookie (name);
    }

    public void header (String header, String value) {
        response.header (header, value);
    }

    public void body (String body) {
        response.body (body);
    }

    public void cookie (String path, String name, String value, int maxAge, boolean secured) {
        response.cookie (path, name, value, maxAge, secured);
    }

    public void redirect (String location, int httpStatusCode) {
        response.redirect (location, httpStatusCode);
    }

    public void cookie (String name, String value) {
        response.cookie (name, value);
    }

    public void cookie (String name, String value, int maxAge, boolean secured) {
        response.cookie (name, value, maxAge, secured);
    }

    public void redirect (String location) {
        response.redirect (location);
    }

    public void type (String contentType) {
        response.type (contentType);
    }

    public HttpServletRequest responseRaw () {
        return request.raw ();
    }

    public String responseBody () {
        return request.body ();
    }

    public static void halt () {
        AbstractRoute.halt ();
    }

    public static void halt (int status) {
        AbstractRoute.halt (status);
    }

    public static void halt (String body) {
        AbstractRoute.halt (body);
    }

    public static void halt (int status, String body) {
        AbstractRoute.halt (status, body);
    }
}
