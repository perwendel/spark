package spark.http.matching;

import spark.QueryParamsMap;
import spark.Request;
import spark.routematch.RouteMatch;

import java.util.*;

public class RedispatchRequestWrapper extends RequestWrapper {

    private String queryString;

    public RedispatchRequestWrapper(String redispatchAddress, RouteMatch match, Request request) {
        setDelegate(request);
        queryString = redispatchAddress.replaceAll("^.*?\\?", "");
        changeWrapperMatch(match);
    }

    @Override
    public Map<String, String> params() {
        return this.params;
    }

    @Override
    public String params(String param) {
        return this.params.get(param);
    }

    @Override
    public String queryParams(String queryParam) {
        return queryMap.value(queryParam);
    }

    @Override
    public String[] queryParamsValues(String queryParam) {
        return queryMap.toMap().get(queryParam);
    }

    @Override
    public Set<String> queryParams() {
        return queryMap().toMap().keySet();
    }

    @Override
    public String queryString() {
        if (queryString.equals("")) {
            return null;
        } else {
            return queryString;
        }
    }

    @Override
    public QueryParamsMap queryMap() {
        if (queryMap == null) {
            initQueryMap();
        }
        return queryMap;
    }

    @Override
    public QueryParamsMap queryMap(String key) {
        return queryMap().get(key);
    }

    //TODO: Improve
    private void initQueryMap() {
        Map<String, String[]> resultMap = new HashMap<>();
        String query = queryString.replaceFirst("\\?", "");
        String[] querySplit = query.split("&");
        for (String rawItem : querySplit) {
            String[] item = rawItem.split("=", 2);
            String key = item[0];
            if (!resultMap.containsKey(key)) {
                resultMap.put(key, item.length > 0 ? new String[]{item[1]} : null);
            } else {
                updateParamValues(resultMap, key, item);
            }
        }
        queryMap = new QueryParamsMap(resultMap);
    }

    private void updateParamValues(Map<String, String[]> resultMap, String key, String[] item) {
        String[] values = resultMap.get(key);
        if (item.length > 0) {
            String[] expandedArray = Arrays.copyOf(values, values.length + 1);
            expandedArray[expandedArray.length - 1] = item[1];
            resultMap.put(key, expandedArray);
        } else {
            resultMap.put(key, values);
        }
    }
}
