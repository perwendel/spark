package spark.servlet;

import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;

class FilterTools {

    private static final String SLASH_WILDCARD = "/*";
    private static final String SLASH = "/";
    private static final String FILTER_MAPPING_PARAM = "filterMappingUrlPattern";
    
    static String getRelativePath(HttpServletRequest request, String filterPath) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        path = path.substring(contextPath.length());

        if (path.length() > 0) {
            path = path.substring(1);
        }

        if (!path.startsWith(filterPath)) {
            if (filterPath.equals(path + SLASH)) {
                path += SLASH;
            }
        }
        if (path.startsWith(filterPath)) {
            path = path.substring(filterPath.length());
        }
        
        if (!path.startsWith(SLASH)) {
            path = SLASH + path;
        }

        return path;
    }

    static String getFilterPath(FilterConfig config) {
        String result = config.getInitParameter(FILTER_MAPPING_PARAM);
        if (result == null || result.equals(SLASH_WILDCARD)) {
            return "";
        } else if (!result.startsWith(SLASH) || !result.endsWith(SLASH_WILDCARD)) {
            throw new RuntimeException("The " + FILTER_MAPPING_PARAM + " must start with \"/\" and end with \"/*\". It's: " + result);
        }
        return result.substring(1, result.length() - 1);
    }
    
}
