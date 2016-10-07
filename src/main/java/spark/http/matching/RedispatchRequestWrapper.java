package spark.http.matching;

import spark.QueryParamsMap;
import spark.Request;
import spark.routematch.RouteMatch;

import java.util.*;

public class RedispatchRequestWrapper extends RequestWrapper {

    private String queryString;
    private String body;


    public RedispatchRequestWrapper(String redispatchAddress, RouteMatch match, Request request) {
        this(redispatchAddress, match, request, request.body());
    }

    public RedispatchRequestWrapper(String redispatchAddress, RouteMatch match, Request request, String body) {
        setDelegate(request);
        queryString = redispatchAddress.replaceAll("^.*?\\?", "");
        changeWrapperMatch(match);
        this.body = body;
    }

    @Override
    public String body() {
        return body;
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

    private void initQueryMap() {
        Map<String, String[]> resultMap = new HashMap<>();
        String query = queryString.replaceFirst("\\?", "");
        String[] querySplit = query.split("&");

        for (String rawQueryItem : querySplit) {
            String[] queryItem = rawQueryItem.split("=", 2);
            String queryKey = queryItem[0];
            boolean alreadyHasKey = !resultMap.containsKey(queryKey);

            if (alreadyHasKey) {
                resultMap.put(queryKey, queryItem.length > 0 ? new String[]{queryItem[1]} : null);
            } else {
                updateQueryValue(resultMap, queryKey, queryItem);
            }
        }
        queryMap = new QueryParamsMap(resultMap);
    }

    private void updateQueryValue(Map<String, String[]> resultMap, String key, String[] item) {
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
