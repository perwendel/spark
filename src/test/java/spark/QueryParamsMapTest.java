package spark;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;



public class QueryParamsMapTest {

    QueryParamsMap queryMap = new QueryParamsMap();
    
    @Test
    public void constructorWithParametersMap() {
        Map<String,String> params = new HashMap<String,String>();
        
        params.put("user[info][name]","fede");
        
        QueryParamsMap queryMap = new QueryParamsMap(params);
        
        assertEquals("fede",queryMap.queryMap("user").queryMap("info").queryMap("name").value());
    }
    
    @Test
    public void keyToMap() {
        QueryParamsMap queryMap = new QueryParamsMap();
        
        queryMap.keyToMap(queryMap,"user[name][more]","fede");

        assertFalse(queryMap.queryMap.isEmpty());
        assertFalse(queryMap.queryMap.get("user").queryMap.isEmpty());
        assertFalse(queryMap.queryMap.get("user").queryMap.get("name").queryMap.isEmpty());
        assertEquals("fede",queryMap.queryMap.get("user").queryMap.get("name").queryMap.get("more").value);
    }    
    
    
    @Test
    public void parseKeyShouldParseRootKey() {
        String[] parsed = queryMap.parseKey("user[name][more]");
        
        assertEquals("user",parsed[0]);
        assertEquals("[name][more]",parsed[1]);
    }
    
    @Test
    public void parseKeyShouldParseSubkeys() {
        String[] parsed = null;
        
        parsed = queryMap.parseKey("[name][more]");
        
        assertEquals("name",parsed[0]);
        assertEquals("[more]",parsed[1]);
        
        parsed = queryMap.parseKey("[more]");
        
        assertEquals("more",parsed[0]);
        assertEquals("",parsed[1]);
    }
}
