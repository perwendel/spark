package spark.route;

import java.util.List;

public class Util {

    public static boolean equals(List<RouteEntry> routes, List<RouteEntry> expectedRoutes) {
        if (routes.size() != expectedRoutes.size()) {
            return false;
        }
        for (int i = 0; i < routes.size(); i++) {
            if (!routes.get(i).acceptedType.equals(expectedRoutes.get(i).acceptedType)) {
                return false;
            }
            if (!routes.get(i).httpMethod.equals(expectedRoutes.get(i).httpMethod)) {
                return false;
            }
            if (!routes.get(i).path.equals(expectedRoutes.get(i).path)) {
                return false;
            }
            if (!routes.get(i).target.equals(expectedRoutes.get(i).target)) {
                return false;
            }
        }
        return true;
    }
}
