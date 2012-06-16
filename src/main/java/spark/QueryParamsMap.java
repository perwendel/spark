package spark;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParamsMap {

    protected Map<String,QueryParamsMap> queryMap = new HashMap<String,QueryParamsMap>();
    protected String value;

    protected QueryParamsMap(){

    }

    protected QueryParamsMap(Map<String,String> params) {
        for (Map.Entry<String,String> param : params.entrySet()) {
            keyToMap(this,param.getKey(),param.getValue());
        }
    }

    protected void keyToMap(QueryParamsMap queryMap,String key,String value) {
        String[] parsed = parseKey(key);

        if (!queryMap.queryMap.containsKey(parsed[0]))
            queryMap.queryMap.put(parsed[0],new QueryParamsMap());

        if (!parsed[1].isEmpty())
            keyToMap(queryMap.queryMap.get(parsed[0]),parsed[1],value);
        else
            queryMap.queryMap.get(parsed[0]).value = value; 
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

    public QueryParamsMap queryMap(String key) {
        return queryMap.get(key);
    }

    public String value() {
        return value;
    }

    public String value(String key) {
        return queryMap.get(key).value();
    }

}
