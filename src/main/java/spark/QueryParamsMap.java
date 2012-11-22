package spark;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * This objects represent the parameters sent on a Http Request.
 * <br>
 * Parses parameters keys like in Sinatra. 
 * <br><br>
 * For a querystring like:
 * <br>
 * <code>
 * user[name]=federico&user[lastname]=dayan
 * </code>
 * <br><br>
 * We get would get a structure like: <br>
 * <code>
 *  user : {name: federico, lastname: dayan}
 * </code>
 *
 * <br><br>
 * That is:<br>
 * <code>
 *  queryParamsMapInstance.get("user).get("name").value(); <br>
 *  queryParamsMapInstance.get("user).get("lastname").value();
 * <code>
 * 
 * <br><br>
 * 
 * It is null safe, meaning that if a key does not exist, it does not throw <code>NullPointerExcetpion</code>, it just returns <code>null</code>.
 * @author fddayan
 *
 */
public class QueryParamsMap {
    
    private static QueryParamsMap NULL = new NullQueryParamsMap();

    /** Holds the nested keys */
    protected Map<String,QueryParamsMap> queryMap = new HashMap<String,QueryParamsMap>();
    
    /** Value(s) for this key */
    protected String[] values;
    
    /** Name of this key */
    protected String name;
    
    private Pattern p = Pattern.compile("\\A[\\[\\]]*([^\\[\\]]+)\\]*");

    /**
     * Creates a new QueryParamsMap from and HttpServletRequest.
     * <br>
     * Parses the parameters from request.getParameterMap()
     * <br>
     * No need to decode, since HttpServletRequest does it for us.
     * 
     * @param request
     */
    @SuppressWarnings("unchecked")
    public QueryParamsMap(HttpServletRequest request) {
        if (request == null) throw new IllegalArgumentException("HttpServletRequest cannot be null.");
        loadQueryString(request.getParameterMap());
    }

    
    //Just for testing
    protected QueryParamsMap() {
    }

    protected QueryParamsMap(String name){
        this.name = name;
    }
    
    /**
     * Parses the key and creates the child QueryParamMaps
     * 
     * user[info][name] creates 3 nested QueryParamMaps. For user, info and name.
     * 
     * @param key The key in the formar fo key1[key2][key3] (for example: user[info][name]).
     * @param values
     */
    protected QueryParamsMap(String key, String...values) {
        loadKeys(key,values);
    }

    protected QueryParamsMap(Map<String,String[]> params) {
        loadQueryString(params);
    }

    protected void loadQueryString(Map<String, String[]> params) {
        for (Map.Entry<String,String[]> param : params.entrySet()) {
            loadKeys(param.getKey(),param.getValue());
        }
    }
    
    protected void loadKeys(String key, String[] value) {
        String[] parsed = parseKey(key);
        
        if (parsed == null) return;

        if (!queryMap.containsKey(parsed[0]))
            queryMap.put(parsed[0],new QueryParamsMap(parsed[0]));

        if (!parsed[1].isEmpty())
            queryMap.get(parsed[0]).loadKeys(parsed[1],value);
        else
            queryMap.get(parsed[0]).values = value; 
    }

    protected String[] parseKey(String key) {
        Matcher m = p.matcher(key);

        if (m.find())
            return new String[] {cleanKey(m.group()),key.substring(m.end())};
        else
            return null;
    }

    protected String cleanKey(String group) {
        if (group.startsWith("["))
            return group.substring(1,group.length()-1);
        else 
            return group;
    }

    /**
     * Retruns and element fro the specified key.
     * <br>
     * For querystring: <br><br>
     * <code>
     * user[name]=fede
     * <br>
     * <br>
     * get("user").get("name").value() #  fede
     * <br>
     * or
     * <br>
     * get("user","name").value() #  fede
     * 
     * </code>
     * 
     * @param key The paramater nested key
     * @return
     */
    public QueryParamsMap get(String...keys) {
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
     * Returns the value for this key.
     * <br>
     * If this key has nested elements and does not have a value returns null.
     * 
     * @return
     */
    public String value() {
        if (hasValue())
            return values[0];
        else 
            return null;
    }

    /**
     * Returns the value for that key.
     * <br>
     * 
     * It is a shortcut for: <br><br>
     * <code>
     * get("user").get("name").value()
     * get("user").value("name")
     * </code>
     * @param key
     * @return
     */
    public String value(String...keys) {
        return get(keys).value();
    }
    
    public boolean hasKeys() {
        return !this.queryMap.isEmpty();
    }
    
    public boolean hasValue() {
        return this.values!=null && this.values.length>0;
    }
    
    public Boolean booleanValue() {
      return hasValue()?Boolean.valueOf(value()):null;  
    }
    
    public Integer integerValue() {
        return hasValue()?Integer.valueOf(value()):null;
    }
    
    public Long longValue() {
        return hasValue()?Long.valueOf(value()):null;
    }
    
    public Float floatValue() {
        return hasValue()?Float.valueOf(value()):null;   
    }
    
    public Double doubleValue() {
        return hasValue()?Double.valueOf(value()):null;
    }
    
    public String[] values() {
        return this.values;
    }
    
    private static class NullQueryParamsMap  extends QueryParamsMap {
        public NullQueryParamsMap() {
            super("null");
        } 
    }

    public Map<String, String[]> toMap() {
        Map<String,String[]> map = new HashMap<String,String[]>();
        
        for (Entry<String, QueryParamsMap> key : this.queryMap.entrySet()) {
            map.put(key.getKey(),key.getValue().values);
        }
        
        return map;
    }
}


