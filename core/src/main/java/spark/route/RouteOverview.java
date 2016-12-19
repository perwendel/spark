/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.route;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.utils.Wrapper;
import sun.reflect.ConstantPool;

import static java.util.Collections.singletonList;
import static spark.Spark.get;

public class RouteOverview {

    /**
     * Enables a route overview (showing all the mapped routes)
     * The overview is made available at "/debug/routeoverview/"
     * Calling this method before any other route mapping
     * bas been performed will initialize the Spark server
     */
    public static void enableRouteOverview() {
        enableRouteOverview("/debug/routeoverview/");
    }

    /**
     * Enables a route overview (showing all the mapped routes)
     * The overview is made available at the provided path
     *
     * @param path the path to the route overview
     *             Calling this method before any other route mapping
     *             bas been performed will initialize the Spark server
     */
    public static void enableRouteOverview(String path) {
        get(path, RouteOverview::createHtmlOverview);
    }

    // Everything below this point is either package private or private

    static List<RouteEntry> routes = new ArrayList<>();

    static void add(RouteEntry entry, Object wrapped) {

        if (wrapped instanceof Wrapper) {
            entry.target = ((Wrapper) wrapped).delegate();
        }

        routes.add(entry);
    }

    static String createHtmlOverview(Request request, Response response) {
        String head = "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<style>b,thead{font-weight:700}body{font-family:monospace;padding:15px}table{border-collapse:collapse;font-size:14px;border:1px solid #d5d5d5;width:100%;white-space:pre}thead{background:#e9e9e9;border-bottom:1px solid #d5d5d5}tbody tr:hover{background:#f5f5f5}td{padding:6px 15px}b{color:#33D}em{color:#666}</style>";

        String rowTemplate = "<tr><td>%s</td><td>%s</td><td><b>%s</b></td><td>%s</td></tr>";

        List<String> tableContent = new ArrayList<>(singletonList("<thead><tr><td>Method</td><td>Accepts</td><td>Path</td><td>Route</td></tr></thead>"));

        routes.forEach(r -> {
            tableContent.add(String.format(rowTemplate, r.httpMethod.name(), r.acceptedType.replace("*/*", "any"), r.path, createHtmlForRouteTarget(r.target)));
        });

        return head + "<body><h1>All mapped routes</h1><table>" + String.join("", tableContent) + "</table><body>";
    }

    static String createHtmlForRouteTarget(Object routeTarget) {
        String routeStr = routeTarget.toString();

        if (routeStr.contains("$$Lambda$")) { // This is a Route or Filter lambda

            Map<Object, String> fieldNames = fieldNames(routeTarget);
            String className = routeStr.split("\\$")[0];

            if (fieldNames.containsKey(routeTarget)) { // Lambda name has been mapped in fieldNameMap
                return className + "<b>." + fieldNames.get(routeTarget) + "</b> <em>(field)</em>";
            }

            if (!methodName(routeTarget).contains("lambda$")) { // Method has name (is not anonymous lambda)
                return className(routeTarget) + "<b>::" + methodName(routeTarget)
                        + "</b> <em>(method reference)</em>";
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

    private static Map<Object, String> fieldNames(Object routeTarget) {
        Map<Object, String> fieldNames = new HashMap<>();

        try {
            Class clazz = Class.forName(className(routeTarget));

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fieldNames.put(field.get(field), field.getName());
            }

        } catch (Exception ignored) { // Nothing really matters.
        }

        return fieldNames;
    }

    private static String className(Object routeTarget) {
        return methodRefInfo(routeTarget)[0].replace("/", ".");
    }

    private static String methodName(Object routeTarget) {
        return methodRefInfo(routeTarget)[1];
    }

    private static String[] methodRefInfo(Object routeTarget) {
        try {
            // This is some robustified shit right here.
            Method getConstantPool = Class.class.getDeclaredMethod("getConstantPool");
            getConstantPool.setAccessible(true);

            ConstantPool constantPool = (ConstantPool) getConstantPool.invoke(routeTarget.getClass());
            return constantPool.getMemberRefInfoAt(constantPool.getSize() - 2);
        } catch (Exception e) {
            return new String[] {"", ""};
        }
    }

}
