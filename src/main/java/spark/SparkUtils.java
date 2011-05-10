package spark;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SparkUtils {

    public static List<String> convertToList(String route) {
        String[] pathArray = route.split("/");
        List<String> path = new ArrayList<String>();
        for (String p : pathArray) {
            if (p.length() > 0) {
                path.add(p);
                System.out.println("p: " + p);
            }
        }
        return path;
    }
    
    public static boolean isParam(String routePart) {
        return routePart.startsWith(":");
    }
    
    public static String getParamName(String routePart) {
        return routePart.substring(1);
    }
 
    public static boolean hasWebContext(Method m) {
        Class<?>[] parameterTypes = m.getParameterTypes();
        return (parameterTypes.length == 1 && parameterTypes[0].equals(WebContext.class));
    }
    
}
