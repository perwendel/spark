package spark.route;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.FilterImpl;
import spark.Request;
import spark.Response;
import spark.RouteImpl;
import sun.reflect.ConstantPool;

import static java.util.Collections.singletonList;
import static spark.Spark.get;

public class RouteOverview {

    /**
     * Enables a route overview (showing all the mapped routes)
     * The overview is made available at "/debug/routeOverview/"
     */
    public static void enableRouteOverview() {
        get("/debug/routeOverview/", RouteOverview::createHtmlOverview);
    }

    /**
     * Enables a route overview (showing all the mapped routes)
     * The overview is made available at the provided path
     * @param path the path to the route overview
     */
    public static void enableRouteOverview(String path) {
        get(path, RouteOverview::createHtmlOverview);
    }

    // Everything below this point is either package private or private

    static List<RouteEntry> routes = new ArrayList<>();

    static void add(RouteEntry entry, Object target) {
        if (target instanceof RouteImpl) {
            entry.target = ((RouteImpl) target).getRoute();
        }
        if (target instanceof FilterImpl) {
            entry.target = ((FilterImpl) target).getFilter();
        }
        routes.add(entry);
    }

    private static String createHtmlOverview(Request request, Response response) {
        String head = "<meta name='viewport' content='width=device-width, initial-scale=1'>" + "<style>b,thead{font-weight:700}body{font-family:monospace;padding:15px}table{border-collapse:collapse;font-size:14px;border:1px solid #d5d5d5;width:100%;white-space:pre}thead{background:#e9e9e9;border-bottom:1px solid #d5d5d5}tbody tr:hover{background:#f5f5f5}td{padding:6px 15px}b{color:#33D}em{color:#666}</style>";
        String rowTemplate = "<tr><td>%s</td><td>%s</td><td><b>%s</b></td><td>%s</td></tr>";
        List<String> tableContent = new ArrayList<>(singletonList("<thead><tr><td>Method</td><td>Accepts</td><td>Path</td><td>Route</td></tr></thead>"));
        Map<Object, String> fieldNameMap = new HashMap<>();
        routes.forEach(r -> {
            addFieldsFromTargetToNameMap(r.target, fieldNameMap);
            tableContent.add(String.format(rowTemplate, r.httpMethod.name(), r.acceptedType.replace("*/*", "any"), r.path, createHtmlForRouteHandler(r.target, fieldNameMap)));
        });
        return head + "<body><h1>All mapped routes</h1><table>" + String.join("", tableContent) + "</table><body>";
    }

    private static String createHtmlForRouteHandler(Object routeTarget, Map<Object, String> fieldNameMap) {
        String routeStr = routeTarget.toString();
        if (routeStr.contains("$$Lambda$")) { // This is a Route or Filter lambda
            String className = routeStr.split("\\$")[0];
            if (fieldNameMap.containsKey(routeTarget)) { // Lambda name has been mapped in fieldNameMap
                return className + "<b>." + fieldNameMap.get(routeTarget) + "</b> <em>(field)</em>";
            }
            if (!getMethodName(routeTarget).contains("lambda$")) { // Method has name (is not anonymous lambda)
                return getClassName(routeTarget) + "<b>::" + getMethodName(routeTarget) + "</b> <em>(method reference)</em>";
            }
            return className + "<b>???</b> <em>(anonymous lambda)</em>";
        }
        if (routeStr.contains("@")) { // This is a Class implementing Route or Filter
            String packages = routeStr.split("@")[0].substring(0, routeStr.lastIndexOf("."));
            String className = routeStr.split("@")[0].substring(routeStr.lastIndexOf(".") + 1);
            return packages + ".<b>" + className + ".class</b> <em>(class)</em>";
        }
        return "<b>Mysterious route handler</b>";
    }

    private static void addFieldsFromTargetToNameMap(Object routeTarget, Map<Object, String> fieldNameMap) {
        try {
            Class clazz = Class.forName(getClassName(routeTarget));
            for (Field field : clazz.getDeclaredFields()) {
                fieldNameMap.put(field.get(field), field.getName());
            }
        } catch (Exception ignored) { // Nothing really matters.
        }
    }

    private static String getClassName(Object routeTarget) {
        return getMethodRefInfo(routeTarget)[0].replace("/", ".");
    }

    private static String getMethodName(Object routeTarget) {
        return getMethodRefInfo(routeTarget)[1];
    }

    private static String[] getMethodRefInfo(Object routeTarget) {
        try {
            // This is some robustified shit right here.
            Method getConstantPool = Class.class.getDeclaredMethod("getConstantPool");
            getConstantPool.setAccessible(true);
            ConstantPool constantPool = (ConstantPool) getConstantPool.invoke(routeTarget.getClass());
            return constantPool.getMemberRefInfoAt(constantPool.getSize() - 2);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

}
