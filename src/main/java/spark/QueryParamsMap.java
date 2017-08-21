package spark;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * This objects represent the parameters sent on a Http Request. <br>
 * Parses parameters keys like in Sinatra. <br>
 * <br>
 * For a querystring like: <br>
 * user[name]=federico&#38;user[lastname]=dayan
 * <br>
 * <br>
 * We get would get a structure like: <br>
 * user : {name: federico, lastname: dayan}
 * <br>
 * <br>
 * That is:<br>
 * queryParamsMapInstance.get("user").get("name").value(); <br>
 * queryParamsMapInstance.get("user").get("lastname").value();
 * <br><br>
 * It is null safe, meaning that if a key does not exist, it does not throw NullPointerException
 * , it just returns null.
 *
 * @author fddayan
 */
public class QueryParamsMap {

    private static final QueryParamsMap NULL = new NullQueryParamsMap();

    /**
     * Pattern for parsing the key of querystring
     */
    private static final Pattern KEY_PATTERN = Pattern.compile("\\A[\\[\\]]*([^\\[\\]]+)\\]*");

    /**
     * Holds the nested keys
     */
    private Map<String, QueryParamsMap> queryMap = new HashMap<>();

    /**
     * Value(s) for this key
     */
    private String[] values;

    /**
     * Creates a new QueryParamsMap from and HttpServletRequest. <br>
     * Parses the parameters from request.getParameterMap() <br>
     * No need to decode, since HttpServletRequest does it for us.
     *
     * @param request the servlet request
     */
    public QueryParamsMap(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest cannot be null.");
        }
        loadQueryString(request.getParameterMap());
    }

    // Just for testing
    protected QueryParamsMap() {
    }


    /**
     * Parses the key and creates the child QueryParamMaps
     * user[info][name] creates 3 nested QueryParamMaps. For user, info and
     * name.
     *
     * @param key    The key in the formar fo key1[key2][key3] (for example:
     *               user[info][name]).
     * @param values the values
     */
    protected QueryParamsMap(String key, String... values) {
        loadKeys(key, values);
    }

    /**
     * Constructor
     *
     * @param params the parameters
     */
    protected QueryParamsMap(Map<String, String[]> params) {
        loadQueryString(params);
    }

    /**
     * loads query string
     *
     * @param params the parameters
     */
    protected final void loadQueryString(Map<String, String[]> params) {
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            loadKeys(param.getKey(), param.getValue());
        }
    }

    /**
     * loads keys
     *
     * @param key   the key
     * @param value the values
     */
    protected final void loadKeys(String key, String[] value) {
        String[] parsed = parseKey(key);

        if (parsed == null) {
            return;
        }

        if (!queryMap.containsKey(parsed[0])) {
            queryMap.put(parsed[0], new QueryParamsMap());
        }
        if (!parsed[1].isEmpty()) {
            queryMap.get(parsed[0]).loadKeys(parsed[1], value);
        } else {
            queryMap.get(parsed[0]).values = value.clone();
        }
    }

    protected final String[] parseKey(String key) {
        Matcher m = KEY_PATTERN.matcher(key);

        if (m.find()) {
            return new String[] {cleanKey(m.group()), key.substring(m.end())};
        } else {
            return null; // NOSONAR
        }
    }

    protected static final String cleanKey(String group) {
        if (group.startsWith("[")) {
            return group.substring(1, group.length() - 1);
        } else {
            return group;
        }
    }

    /**
     * Returns an element from the specified key. <br>
     * For querystring: <br>
     * <br>
     * user[name]=fede
     * <br>
     * <br>
     * get("user").get("name").value() #  fede
     * <br>
     * or
     * <br>
     * get("user","name").value() #  fede
     *
     * @param keys The parameter nested key(s)
     * @return the query params map
     */
    public QueryParamsMap get(String... keys) {
        QueryParamsMap ret = this;
        for (String key : keys) {
            if (ret.queryMap.containsKey(key)) {
                ret = ret.queryMap.get(key);
            } else {
                ret = NULL;
            }
        }
        return ret;
    }

    /**
     * Returns the value for this key. <br>
     * If this key has nested elements and does not have a value returns null.
     *
     * @return the value
     */
    public String value() {
        if (hasValue()) {
            return values[0];
        } else {
            return null;
        }
    }

    /**
     * Returns the value for that key. <br>
     * It is a shortcut for: <br>
     * <br>
     * <code>
     * get("user").get("name").value()
     * get("user").value("name")
     * </code>
     *
     * @param keys the key(s)
     * @return the value
     */
    public String value(String... keys) {
        return get(keys).value();
    }

    /**
     * @return has keys
     */
    public boolean hasKeys() {
        return !this.queryMap.isEmpty();
    }

    /**
     * @return true if the map contains the given key
     */
    public boolean hasKey(String key) {
    	return this.queryMap.containsKey( key );
    }

    /**
     * @return has values
     */
    public boolean hasValue() {
        return this.values != null && this.values.length > 0;
    }

    /**
     * @return the boolean value
     */
    public Boolean booleanValue() {
        return hasValue() ? Boolean.valueOf(value()) : null;
    }

    /**
     * @return the integer value
     */
    public Integer integerValue() {
        return hasValue() ? Integer.valueOf(value()) : null;
    }

    /**
     * @return the long value
     */
    public Long longValue() {
        return hasValue() ? Long.valueOf(value()) : null;
    }

    /**
     * @return the float value
     */
    public Float floatValue() {
        return hasValue() ? Float.valueOf(value()) : null;
    }

    /**
     * @return the double value
     */
    public Double doubleValue() {
        return hasValue() ? Double.valueOf(value()) : null;
    }

    /**
     * @return the values
     */
    public String[] values() {
        return this.values.clone();
    }

    /**
     * @return the queryMap
     */
    Map<String, QueryParamsMap> getQueryMap() {
        return queryMap;
    }

    /**
     * @return the values
     */
    String[] getValues() {
        return values;
    }


    private static class NullQueryParamsMap extends QueryParamsMap {
        public NullQueryParamsMap() {
            super();
        }
    }

    /**
     * @return Map representation
     */
    public Map<String, String[]> toMap() {
        Map<String, String[]> map = new HashMap<>();

        for (Entry<String, QueryParamsMap> key : this.queryMap.entrySet()) {
            map.put(key.getKey(), key.getValue().values);
        }

        return map;
    }
}
