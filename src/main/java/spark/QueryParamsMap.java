package spark;

import java.util.HashMap;
import java.util.Map;
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
 * It is null safe, meaning that if a key does not exist, it does not throw <code>NullPointerExcetpion</code>, it just returns <code>null</code>
 * @author fddayan
 *
 */
public class QueryParamsMap {
    
    private static QueryParamsMap NULL = new NullQueryParamsMap();

    protected Map<String,QueryParamsMap> queryMap = new HashMap<String,QueryParamsMap>();
    protected String[] values;
    protected String name;

    /**
     * Creates a new QueryParamsMap from and HttpServletRequest.
     * <br>
     * Parses the parameters from request.getParameterMap()
     * <br>
     * No need to decode, since HttpServletRequest does it for us.
     * 
     * @param request
     */
    public QueryParamsMap(HttpServletRequest request) {
        if (request == null) throw new IllegalArgumentException("HttpServletRequest cannot be null.");
        load(request.getParameterMap());
    }

    
    //Just for testing
    QueryParamsMap() {
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
        keyToMap(this,key,values);
    }

    protected QueryParamsMap(Map<String,String[]> params) {
        load(params);
    }

    private void load(Map<String, String[]> params) {
        for (Map.Entry<String,String[]> param : params.entrySet()) {
            keyToMap(this,param.getKey(),param.getValue());
        }
    }

    protected void keyToMap(QueryParamsMap queryMap,String key,String[] value) {
        String[] parsed = parseKey(key);

        if (!queryMap.queryMap.containsKey(parsed[0]))
            queryMap.queryMap.put(parsed[0],new QueryParamsMap(parsed[0]));

        if (!parsed[1].isEmpty())
            keyToMap(queryMap.queryMap.get(parsed[0]),parsed[1],value);
        else
            queryMap.queryMap.get(parsed[0]).values = value; 
    }

    protected String[] parseKey(String key) {
        Pattern p = Pattern.compile("\\A[\\[\\]]*([^\\[\\]]+)\\]*");
        Matcher m = p.matcher(key);

        if (m.find())
            return new String[] {clean(m.group()),key.substring(m.end())};
        else
            return null;
    }

    protected String clean(String group) {
        if (group.startsWith("["))
            return group.substring(1,group.length()-1);
        else 
            return group;
    }

    public QueryParamsMap get(String key) {
        if (queryMap.containsKey(key)) {
            return queryMap.get(key);
        } else {
            return NULL;
        }
    }

    public String value() {
        if (hasValue())
            return values[0];
        else 
            return null;
    }

    public String value(String key) {
        if (queryMap.containsKey(key))
            return queryMap.get(key).value();
        else
            return null;
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
}


